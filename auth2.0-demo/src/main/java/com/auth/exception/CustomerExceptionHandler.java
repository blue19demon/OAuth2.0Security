package com.auth.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.auth.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomerExceptionHandler {

	@ExceptionHandler(value = HttpClientErrorException.class)
	public ResponseVO handle(HttpClientErrorException e) {
		log.error("请求没有权限不被支持 -> {}", e);
		return ResponseVO.error(900,e.getMessage());
	}
}