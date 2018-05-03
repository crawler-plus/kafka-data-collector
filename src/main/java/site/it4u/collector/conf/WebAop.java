package site.it4u.collector.conf;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class WebAop {

    private WebAop() {}

    @Pointcut("execution(* site.it4u.collector.controller.*.*(..))")
    private void webLog() {
    }

    @Pointcut("execution(* site.it4u.collector.exec.*.*(..))")
    private void jobLog() {
    }

    @Around("webLog()")
    private Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Object obj = printLog(proceedingJoinPoint);
        return obj;
    }

    @Around("jobLog()")
    private Object doAroundJobAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        log.info("----------------------job start!!!!!!!!-----------------------------");
        Object obj = printLog(proceedingJoinPoint);
        log.info("----------------------job end!!!!!!!!-----------------------------");
        return obj;
    }

    private Object printLog(ProceedingJoinPoint proceedingJoinPoint) {
        String methodName = proceedingJoinPoint.getSignature().getName();
        long timeStart = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
        }
        log.info("execute method: {}, execute time: {} ms", methodName, (System.currentTimeMillis() - timeStart));
        return obj;
    }
}
