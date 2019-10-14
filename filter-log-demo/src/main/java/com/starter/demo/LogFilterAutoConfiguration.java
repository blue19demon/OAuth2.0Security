package com.starter.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ LogFilterRegistrationBean.class, LogFilter.class })
public class LogFilterAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(LogFilterRegistrationBean.class)
	public LogFilterRegistrationBean logFilterRegistrationBean() {
		return new LogFilterRegistrationBean();
	}
}
