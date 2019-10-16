package com.auth.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Rong.Jia
 * @description: server 配置
 * @date 2019/02/19 15:24:22
 */
@Component
public class ServerConfig {

    @Value("${server.host}")
    private String ip;
    @Value("${server.port}")
    private int serverPort;

    public String getUrl() {
        return "http://" + ip + ":" + this.serverPort;
    }
}