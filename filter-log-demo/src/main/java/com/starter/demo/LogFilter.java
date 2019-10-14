package com.starter.demo;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class LogFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(LogFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info(" logFilter init .. .");
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
        // 从request 中获取到访问的地址，并在控制台中打印出来
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		logger.info(" uri is {} ,param is {}'", URLDecoder.decode(request.getRequestURI(), "UTF-8"),JSONObject.toJSONString(request.getParameterMap()));
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
		logger.info("logFilter destroy ");
	}
}