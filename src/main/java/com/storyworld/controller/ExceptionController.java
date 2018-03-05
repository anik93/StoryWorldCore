package com.storyworld.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler
	public Mono<Object> handleControllerException(HttpServletRequest request, Throwable ex) throws IOException {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		LOG.error("%s %s", request.getReader(), sw.getBuffer().toString());
		return Mono.just("UNEXPECTED ERROR");
	}

}