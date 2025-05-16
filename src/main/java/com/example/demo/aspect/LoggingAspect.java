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
            "!within(com.example.demo.service.BookingHoldSchedulerService) && " +
            "!within(com.example.demo.service.BookingReleaseService)")
    public void logUserPerformAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : "anonymous";

        log.info("{} - User '{}' is invoking '{}'", TenantContext.getCurrentTenant(), username, methodName);
    }

    @Before("within(com.example.demo.service.BookingHoldSchedulerService) && " +
            "within(com.example.demo.service.BookingReleaseService")
    public void logExecution(JoinPoint joinPoint) throws Throwable {
        Long bookingId = (Long) joinPoint.getArgs()[0];
        String methodName = joinPoint.getSignature().getName();

        log.info("{} - Booking #{} is performed {} method",TenantContext.getCurrentTenant(), bookingId, methodName);
    }
}
