package com.auth.domain;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @description 用户登录后返回参数对象
 * @author Zhifeng.Zeng
 * @date 2019/3/7
 */
@Setter
@Getter
@ToString
public class LoginUserVO {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户名
     */
    private String name;
    /**
     * 性别
     */
    private String gender;
    /**
     * 头像
     */
    private String headImageUrl;


}