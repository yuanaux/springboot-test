package com.yuan.controller;

import com.yuan.bean.User;
import com.yuan.utils.PrintLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    User user;

    @PrintLog(info = "测试user")
    @RequestMapping("/user")
    public String getUser(@RequestParam(value = "name", required = false, defaultValue = "先生") String name, @RequestParam(value = "age", required = false, defaultValue = "18") Integer age) throws InterruptedException {
        user.setName(name);
        user.setAge(age);
        System.out.println(user);
        Thread.sleep(5000);
        return "{\"areaCode\":\"\",\"queryItemType\":\"0\",\"svcObjectStruct\":{\"objValue\":\"18084860908\",\"objType\":\"3\",\"dataArea\":\"\",\"objAttr\":\"2\"},\"queryFlag\":\"0\",\"operAttrStruct\":{\"operServiceId\":\"20220106235923000000009541\",\"lanId\":\"\",\"staffId\":\"1100992\",\"operPost\":\"\",\"operOrgId\":\"028006001002\",\"operTime\":\"\"}}";
    }
}
