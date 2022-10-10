package com.yuan.enums;

/**
 * @author: yuanxiaolong
 * @Title: OperationType
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2022/10/10 18:26
 */
public enum OperationType {
    /**
     * 操作类型
     */
    UNKNOWN("unknown"),
    DELETE("delete"),
    SELECT("select"),
    UPDATE("update"),
    INSERT("insert");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    OperationType(String s) {
        this.value = s;
    }
}