package com.funtl.oauth2.resource.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/").hasAuthority("SystemContent")
                .antMatchers("/view/**").hasAuthority("SystemContentView")
                .antMatchers("/insert/**").hasAuthority("SystemContentInsert")
                .antMatchers("/update/**").hasAuthority("SystemContentUpdate")
                .antMatchers("/delete/**").hasAuthority("SystemContentDelete")
                .antMatchers("/category/list").hasAuthority("SystemCategoryView")/*其他资源一律禁止访问，一般不这么用，默认不配置，其他资源都可以匿名访问.anyRequest().denyAll()*/;
  
    /**
     * 
     * 后台server端数据库修改了用户资源权限配置，需要从新启动，做认证才能生效
     * 资源有这么几种访问方式1.POST MAN中选择Authorization->OAuth2.0 填写yourAccessToken访问
     * 2.head中Authorization 填写Authorization：Bearer yourAccessToken这么访问
     * 3.如果是GET请求，url后边跟上参数?access_token=yourAccessToken这么访问
     */
    }

}