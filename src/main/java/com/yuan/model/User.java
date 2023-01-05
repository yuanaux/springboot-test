package com.yuan.model;

import com.yuan.utils.compare.Compare;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {
    @Compare("姓名")
    private String name;

    @Compare("年龄")
    private Integer age;
}
