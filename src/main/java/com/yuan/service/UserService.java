package com.yuan.service;

import com.yuan.bean.User;
import com.yuan.dao.UserDao;
import com.yuan.utils.PrintLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserService {
    @Autowired
    UserDao userDao;

    @PrintLog
    public int saveUser(User user) {
        return userDao.addUser(user);
    }

}
