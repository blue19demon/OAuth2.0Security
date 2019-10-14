package com.starter.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

public class LogFilterRegistrationBean extends FilterRegistrationBean<LogFilter> {
	public LogFilterRegistrationBean() {
		super();
		this.setFilter(new LogFilter()); // 添加LogFilter过滤器
		this.addUrlPatterns("/*");
		this.setName("LogFilter");
		this.setOrder(1); // 设置优先级
	}
}