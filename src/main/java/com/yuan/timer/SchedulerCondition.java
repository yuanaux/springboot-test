package com.yuan.timer;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author: yuanxiaolong
 * @Title: SchedulerCondition
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2023/6/30 11:02
 */
public class SchedulerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return Boolean.valueOf(conditionContext.getEnvironment().getProperty("scheduler.enabled"));
    }
}
