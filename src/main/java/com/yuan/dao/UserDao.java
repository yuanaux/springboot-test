package com.yuan.dao;

import com.yuan.bean.User;
import com.yuan.utils.PrintLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class UserDao {

    @PrintLog
    public int addUser(User user) {
        try {
            Thread.sleep(new Random().nextInt(5) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
