package com.hmall.dao;

import com.hmall.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void selectByPrimaryKey() {
        User selectByPrimaryKey=userMapper.selectByPrimaryKey(1);
    }
}