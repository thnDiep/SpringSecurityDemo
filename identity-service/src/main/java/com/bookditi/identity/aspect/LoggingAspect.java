package com.bookditi.identity.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bookditi.identity.config.tenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@Profile("!test")
public class LoggingAspect {
    @Before("execution(* com.bookditi.identity.service..*.*(..)) && "
            + "!within(com.bookditi.identity.service.BookingHoldSchedulerService) && "
            + "!within(com.bookditi.identity.service.BookingReleaseService)")
    public void logUserPerformAction(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (method.isAnnotationPresent(Scheduled.class)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username =
                (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "anonymous";

        log.info("{} - User '{}' is invoking '{}'", TenantContext.getCurrentTenant(), username, method.getName());
    }

    @Before("within(com.bookditi.identity.service.BookingHoldSchedulerService) && "
            + "within(com.bookditi.identity.service.BookingReleaseService")
    public void logExecution(JoinPoint joinPoint) throws Throwable {
        Long bookingId = (Long) joinPoint.getArgs()[0];
        String methodName = joinPoint.getSignature().getName();

        log.info("{} - Booking #{} is performed {} method", TenantContext.getCurrentTenant(), bookingId, methodName);
    }
}
