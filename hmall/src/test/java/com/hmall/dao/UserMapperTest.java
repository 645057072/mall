package com.hmall.dao;

import com.hmall.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void selectByPrimaryKey() {
        User selectByPrimaryKey=userMapper.selectByPrimaryKey(1);
    }

    @Test
    public void checkByUsername(){
        //ApplicationContext applicationContext =
        int result=userMapper.checkByUsername("geely");
        System.out.println(result);
    }
}