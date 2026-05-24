package com.auth.authservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.auth.authservice.service.impl.*.*(..))")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        String method = jp.getSignature().toShortString();
        log.info("[auth-service] >> {}", method);
        long start = System.currentTimeMillis();
        Object result = jp.proceed();
        log.info("[auth-service] << {} ({}ms)", method, System.currentTimeMillis() - start);
        return result;
    }
}
