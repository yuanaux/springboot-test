package com.yuan.enums;

/**
 * @author: yuanxiaolong
 * @Title: OperationUnit
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2022/10/10 18:28
 */
public enum OperationUnit {
    /**
     * 被操作的单元
     */
    UNKNOWN("unknown"),
    USER("user"),
    EMPLOYEE("employee"),
    Redis("redis");

    private String value;

    OperationUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}