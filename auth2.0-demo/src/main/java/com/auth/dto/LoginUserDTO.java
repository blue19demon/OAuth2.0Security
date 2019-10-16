package com.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @description 登录用户传输参数
 * @author Zhifeng.Zeng
 * @date 2019/4/19 14:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {

    /**
     * 用户名
     */
    private String account;

    /**
     * 用户密码
     */
    private String password;

}