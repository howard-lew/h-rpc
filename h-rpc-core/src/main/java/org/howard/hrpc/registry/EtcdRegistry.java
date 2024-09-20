package org.howard.hrpc.registry;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * etcd 注册中心实现
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;
    /**
     * etcd 根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 本地注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        // 开启心跳检测
        heartbeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8)).get();

        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get().getKvs();

            return kvs.stream()
                    .map(kv -> JSONUtil.toBean(kv.getValue().toString(), ServiceMetaInfo.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败: ", e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        if (client != null) client.close();

        if (kvClient != null) kvClient.close();
    }

    @Override
    public void heartbeat() {
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * ?", (Task) () -> {
            for (String key : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> kvs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                    // 节点已过期，跳过
                    if (CollectionUtil.isEmpty(kvs)) {
                        continue;
                    }
                    // 节点未过期，刷新租约（续签）
                    String valueJsonStr = kvs.get(0).getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(valueJsonStr, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(key + "续签失败: ", e);
                }
            }
        });

        // 支持秒级别定时任务，兼容 Quartz 表达式（5位表达式、6位表达式都兼容）
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {

            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from("test_key".getBytes());
            ByteSequence value = ByteSequence.from("test_value".getBytes());

            // put the key-value
            kvClient.put(key, value).get();

            // get the CompletableFuture
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);

            // get the value from CompletableFuture
            GetResponse response = getFuture.get();
            System.out.println(response);

            // delete the key
            kvClient.delete(key).get();
        }
    }
}
