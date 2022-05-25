package com.yuan.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Aspect
@Component
// @Profile({"dev", "test", "uat"})  // 生效环境配置
public class PrintLogAspect {

    private static final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(com.yuan.utils.PrintLog)")
    public void printLog() {
        // do nothing
    }

    @Before("printLog()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String targetClassName = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        PrintLog printLog = signature.getMethod().getAnnotation(PrintLog.class);
        String desc = printLog.info();

        log.info("Method info: {}.{} {}, params: {}", targetClassName, methodName, desc, JSONObject.toJSONString(getFieldsName(joinPoint)));
    }

    @Around("printLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();

        log.info("Method result: {}, cost: {}ms", JSONObject.toJSONString(result), System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 获取参数列表
     */
    private static Map<String, Object> getFieldsName(JoinPoint joinPoint) {
        // 参数值
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = pnd.getParameterNames(method);
        if (parameterNames != null) {
            Map<String, Object> paramMap = new HashMap<>(parameterNames.length);
            for (int i = 0; i < parameterNames.length; i++) {
                paramMap.put(parameterNames[i], args[i]);
            }
            return paramMap;
        }
        return Collections.emptyMap();
    }
}
