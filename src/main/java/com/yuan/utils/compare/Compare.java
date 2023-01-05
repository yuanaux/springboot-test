package com.yuan.utils.compare;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yuanxiaolong
 * @Title: Compare
 * @ProjectName: springboot-test
 * @Description: 字段标记注解
 * @date: 2023/1/5 18:22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Compare {
    String value();// 字段名称
}