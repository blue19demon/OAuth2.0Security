package com.auth.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhifeng.Zeng
 * @description OAuth2服务器配置
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	public static final String ROLE_ADMIN = "ADMIN";
	// 访问客户端密钥
	public static final String CLIENT_SECRET = "123456";
	// 访问客户端ID
	public static final String CLIENT_ID = "client_1";
	// 鉴权模式
	public static final String GRANT_TYPE[] = { "password", "refresh_token", "authorization_code",
			"client_credentials" };
	@Autowired
	private RedisConnectionFactory connectionFactory;

	@Autowired
	private ServerConfig serverConfig;
	/**
	 * 注入authenticationManager 来支持 password grant type
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Bean
	public RedisTokenStore redisTokenStore() {
		return new RedisTokenStore(connectionFactory);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.realm("oauth2-resources") // code授权添加
				.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()") // allow check token
				.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
				// 允许 GET、POST 请求获取 token，即访问端点：oauth/token
				.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
		
		//token信息存到服务内存
        /*endpoints.tokenStore(new InMemoryTokenStore())
                .authenticationManager(authenticationManager);*/

        //token信息存到redis
        endpoints.tokenStore(redisTokenStore()).authenticationManager(authenticationManager);
        //配置TokenService参数
        DefaultTokenServices tokenService = new DefaultTokenServices();
        tokenService.setTokenStore(endpoints.getTokenStore());
        tokenService.setSupportRefreshToken(true);
        tokenService.setClientDetailsService(endpoints.getClientDetailsService());
        tokenService.setTokenEnhancer(endpoints.getTokenEnhancer());
        //半小时
        tokenService.setAccessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(30));
        //半小时
        tokenService.setRefreshTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(30));
        tokenService.setReuseRefreshToken(false);
        endpoints.tokenServices(tokenService);
        
        // 最后一个参数为替换之后授权页面的url
        endpoints.pathMapping("/oauth/confirm_access","/custom/confirm_access");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(CLIENT_ID).secret(CLIENT_SECRET)
				.redirectUris(serverConfig.getUrl()+"/auth/callBack")// code授权添加
				.authorizedGrantTypes(GRANT_TYPE[0], GRANT_TYPE[1], GRANT_TYPE[2], GRANT_TYPE[3]).scopes("all")
				.resourceIds("oauth2-resource").accessTokenValiditySeconds(1200).refreshTokenValiditySeconds(50000);
	}

}