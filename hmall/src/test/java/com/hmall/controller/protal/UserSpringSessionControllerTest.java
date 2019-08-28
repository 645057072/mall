package com.hmall.controller.protal;

import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSpringSessionControllerTest {
    @Autowired
    private IUserService iUserService;

    @Test
    public void login() {
        ServiceResponse<User> user=iUserService.login("admin","admin");
        System.out.println(user);
    }
}