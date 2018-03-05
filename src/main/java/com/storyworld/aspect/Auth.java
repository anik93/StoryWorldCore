package com.storyworld.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storyworld.annotations.Secure;

@Aspect
@Component
public class Auth {

	@Around(value = "execution(* *()) " + "&& @annotation(com.storyworld.annotations.Secure)")
	public void auth(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		Secure secure = method.getAnnotation(Secure.class);
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
	}

}
