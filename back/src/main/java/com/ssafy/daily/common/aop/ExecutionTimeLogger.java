package com.ssafy.daily.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeLogger {

    @Around("execution(* com.ssafy.daily.reward.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        System.out.println("[응답 시간] " + joinPoint.getSignature().toShortString() + " " + duration + " ms");

        return result;
    }
}
