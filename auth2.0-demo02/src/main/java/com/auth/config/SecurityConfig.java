package com.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import com.auth.domain.CustomUserDetail;
import com.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserRepository userRepository;

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 将 check_token 暴露出去，否则资源服务器访问时报 403 错误
		web.ignoring().antMatchers("/oauth/check_token");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//"/oauth/**", "/login/**","/login","/authConfim", "/logout/**"
		http.requestMatchers().antMatchers("/login", "/auth/authorize","/oauth/authorize").and().authorizeRequests()
				.antMatchers("/css/**").permitAll().antMatchers("/oauth/**").authenticated().and().formLogin()
				.loginPage("/login").failureUrl("/login?error")
				 // 登录处理url
                .loginProcessingUrl("/auth/authorize").permitAll() // 登录页面用户任意访问
				.permitAll()
				.and()
                .logout().permitAll();
		http.httpBasic().disable();
	}

	// 配置内存模式的用户
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				log.info("username:{}", username);
				com.auth.entity.User user = userRepository.findUserByAccount(username);
				if (user != null) {
					CustomUserDetail customUserDetail = new CustomUserDetail();
					customUserDetail.setUsername(user.getAccount());
					customUserDetail.setPassword(user.getPassword());
					List<GrantedAuthority> list = AuthorityUtils.createAuthorityList(user.getRole().getRole());
					customUserDetail.setAuthorities(list);
					return customUserDetail;
				} else {
					return null;
				}
			}
		};
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

	/**
	 * 需要配置这个支持password模式
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
