package com.yuan.controller;

import com.yuan.model.User;
import com.yuan.service.UserService;
import com.yuan.utils.PrintLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController(value = "/user")
public class UserController {

    @Autowired
    User user;

    @Autowired
    UserService userService;

    @PrintLog
    @GetMapping
    public String getUser(@RequestParam(value = "name", required = false, defaultValue = "先生") String name, @RequestParam(value = "age", required = false, defaultValue = "18") Integer age) throws InterruptedException {
        user.setName(name);
        user.setAge(age);
        userService.saveUser(user);
        return "GET-User：" + user;
    }

    @PostMapping
    public String saveUser() {
        return "POST-User";
    }

    @PutMapping
    public String updateUser() {
        return "PUT-User";
    }

    @DeleteMapping
    public String deleteUser() {
        return "DELETE-User";
    }
}
