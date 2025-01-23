package org.howard.example.springboot.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author <a href="https://github.com/howard-lew">HowardLiu</a>
 */
@SpringBootTest
class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    void test() {
        exampleService.test();
    }
}
