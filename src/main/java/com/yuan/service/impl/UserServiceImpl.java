package com.yuan.service.impl;

import com.yuan.annotation.OperationLogDetail;
import com.yuan.dao.UserDao;
import com.yuan.enums.OperationType;
import com.yuan.enums.OperationUnit;
import com.yuan.model.User;
import com.yuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: yuanxiaolong
 * @Title: UserServiceImpl
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2022/10/11 10:07
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @OperationLogDetail(detail = "通过[{{name}}]获取用户名", level = 3, operationUnit = OperationUnit.USER, operationType = OperationType.SELECT)
    @Override
    public int getUser(User user) {
        return 0;
    }

    @Override
    public int saveUser(User user) {
        return userDao.addUser(user);
    }
}
