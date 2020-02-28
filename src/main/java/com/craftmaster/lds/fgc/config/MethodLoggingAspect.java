package com.craftmaster.lds.fgc.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class MethodLoggingAspect {
  @Around("execution(* com.craftmaster.lds.fgc..*.*(..))")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getName());

    StopWatch stopWatch = new StopWatch();
    try {
      stopWatch.start();
      final var returnValue = joinPoint.proceed();
      stopWatch.stop();

      logger.debug("{} ({}) => {} [{}ms]", joinPoint.getSignature().getName(), joinPoint.getArgs(), returnValue, stopWatch.getTotalTimeMillis());
      return returnValue;
    } catch (Throwable ex) {
      stopWatch.stop();
      logger.debug("{} ({}) threw {} [{}ms]", joinPoint.getSignature().getName(), joinPoint.getArgs(), ex, stopWatch.getTotalTimeMillis());

      throw ex;
    }
  }
}
