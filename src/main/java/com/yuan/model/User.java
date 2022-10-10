package com.yuan.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {
    private String name;
    private Integer age;
}
