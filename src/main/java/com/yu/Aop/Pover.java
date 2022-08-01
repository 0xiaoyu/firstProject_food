package com.yu.Aop;


import com.yu.common.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Aspect
@Component
public class Pover {

    @Pointcut("execution(* com.yu.controller.*.backend*(..))")
    public void backendcut(){}


    @Around("backendcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session=attr.getRequest().getSession(true);
        Object employee = session.getAttribute("employee");
        if (employee!=null)
            return joinPoint.proceed();
        else
            return R.error("未登录");
    }
}
