package com.boot.dbskins.AOP;

import com.boot.dbskins.Exception.ForbiddenException;
import com.boot.dbskins.Security.Service.CustomUserDetailService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IsBlockedCheckAspect {
    private final CustomUserDetailService userDetailsService;

    @Autowired
    public IsBlockedCheckAspect(CustomUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Around("@annotation(com.boot.dbskins.Annotation.IsBlocked)")
    public Object checkUserBlocked(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            if (userDetailsService.isBlocked(username)) {
                throw new ForbiddenException("User is blocked");
            }
        }
        return joinPoint.proceed();
    }
}
