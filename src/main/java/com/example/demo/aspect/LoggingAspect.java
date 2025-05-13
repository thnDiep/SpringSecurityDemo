package com.example.demo.aspect;

import com.example.demo.config.tenancy.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Before("execution(* com.example.demo.service..*.*(..)) && " +
            "!within(com.example.demo.service.BookingService) && " +
            "!within(com.example.demo.service.SeatService) && " +
            "!within(com.example.demo.service.SeatHoldSchedulerService)")
    public void logUserPerformAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "anonymous";

        log.info("{} - User '{}' is invoking '{}'", TenantContext.getCurrentTenant(), username, methodName);
    }

    @Before("within(com.example.demo.service.BookingService)")
    public void logUserPerformActionOnSeat(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("{} - User '{}' performed '{}' on seat '{}'", TenantContext.getCurrentTenant(), username, methodName, joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : "");
    }

    @Before("within(com.example.demo.service.SeatHoldSchedulerService)")
    public void logExecution(JoinPoint joinPoint) throws Throwable {
        String seatId = (String) joinPoint.getArgs()[0];
        String methodName = joinPoint.getSignature().getName();

        log.info("{} - Seat {} - is performed {} method",TenantContext.getCurrentTenant(), seatId, methodName);
    }
}
