package com.hmall.controller.backend;


import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IUserService;
import com.hmall.unit.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user")
public class UserManagerController {
    @Autowired

    private IUserService iUserService;

    @RequestMapping(value = "login.do",method= RequestMethod.POST)
    @ResponseBody
    //管理员登录
    public ServiceResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServiceResponse response= iUserService.login(username,password);
        if (response.isSucess()){
            User user= (User) response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
//                使用session获取用户连接信息
//                session.setAttribute(Const.CURRENT_USER,user);
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                return response;
            }else {
                return ServiceResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;

    }
}
