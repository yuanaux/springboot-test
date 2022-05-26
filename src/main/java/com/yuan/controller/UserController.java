package com.yuan.controller;

import com.yuan.bean.User;
import com.yuan.service.UserService;
import com.yuan.utils.PrintLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    User user;

    @Autowired
    UserService userService;

    @PrintLog
    @RequestMapping("/user")
    public String getUser(@RequestParam(value = "name", required = false, defaultValue = "先生") String name, @RequestParam(value = "age", required = false, defaultValue = "18") Integer age) throws InterruptedException {
        user.setName(name);
        user.setAge(age);
        userService.saveUser(user);
        return "{\"areaCode\":\"\",\"queryItemType\":\"0\",\"svcObjectStruct\":{\"objValue\":\"18084860908\",\"objType\":\"3\",\"dataArea\":\"\",\"objAttr\":\"2\"},\"queryFlag\":\"0\"}";
    }
}
