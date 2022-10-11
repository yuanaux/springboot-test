package com.yuan.service;

import com.yuan.model.User;

public interface UserService {
    int getUser(User user);

    int saveUser(User user);
}
