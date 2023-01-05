package com.yuan.utils.compare;

import lombok.Data;

/**
 * @author: yuanxiaolong
 * @Title: CompareNode
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2023/1/5 18:24
 */
@Data
public class CompareNode {

    private String fieldKey;// 字段
    private Object fieldValue;// 字段值
    private String fieldName;// 字段名称
}