package com.yuan.annotation;

import com.yuan.enums.OperationType;
import com.yuan.enums.OperationUnit;

import java.lang.annotation.*;

/**
 * @author: yuanxiaolong
 * @Title: OperationLogDetail
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2022/10/10 18:18
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogDetail {
    /**
     * 方法描述,可使用占位符获取参数:{{tel}}
     */
    String detail() default "";

    /**
     * 日志等级:自己定，此处分为1-9
     */
    int level() default 0;

    /**
     * 操作类型(enum):主要是select,insert,update,delete
     */
    OperationType operationType() default OperationType.UNKNOWN;

    /**
     * 被操作的对象(此处使用enum):可以是任何对象，如表名(user)，或者是工具(redis)
     */
    OperationUnit operationUnit() default OperationUnit.UNKNOWN;
}
