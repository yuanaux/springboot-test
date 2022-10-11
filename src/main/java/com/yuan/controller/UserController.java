package com.yuan.controller;

import com.yuan.model.User;
import com.yuan.service.UserService;
import com.yuan.utils.PrintLog;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PrintLog
    @GetMapping("/get")
    public String get(User user) throws InterruptedException {
        return "GET-User：" + userService.getUser(user);
    }

    @PrintLog
    @PostMapping("/add")
    public String add(@RequestBody User user) {
        System.out.println(user);
        return "POST-User";
    }

    @PutMapping("/update")
    public String update() {
        return "PUT-User";
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam(value = "id", required = true) Integer id) {
        return "DELETE-User";
    }
}
