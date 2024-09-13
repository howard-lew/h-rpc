package org.howard.example.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 *
 * @Author HowardLiu
 * @Date 2024/9/12
 */
@Data
public class User implements Serializable {
    private String name;
}
