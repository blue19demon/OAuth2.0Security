package com.funtl.oauth2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * 认证服务器，用于获取 Token
 * <p>
 * Description:
 * </p>
 */
@SpringBootApplication
@MapperScan(basePackages = "com.funtl.oauth2.server.mapper")
public class OAuth2ServerApplication {

    /**
     * 1.配置oauth2库的oauth_client_details表
     * 2.去这里授权
     * http://localhost:8070/oauth/authorize?client_id=client&response_type=code
     * 3.curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'grant_type=authorization_code&code=1JuO6V' "http://client:secret@localhost:8070/oauth/token"
                * 去获取token
     */
    public static void main(String[] args) {
        SpringApplication.run(OAuth2ServerApplication.class, args);
    }

}