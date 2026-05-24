package com.bookashow.bookingservice.aspect;

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

    @Around("execution(* com.bookashow.bookingservice.service.impl.*.*(..))")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        String method = jp.getSignature().toShortString();
        log.info("[booking-service] >> {}", method);
        long start = System.currentTimeMillis();
        Object result = jp.proceed();
        log.info("[booking-service] << {} ({}ms)", method, System.currentTimeMillis() - start);
        return result;
    }
}
