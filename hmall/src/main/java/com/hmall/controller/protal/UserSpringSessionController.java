package com.hmall.controller.protal;

import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {
    @Autowired
    private IUserService iUserService;
/*
* 用户登录
*@param:username
*@param:password
* param:session
* */
    @RequestMapping(value = "login.do",method= RequestMethod.POST)
    @ResponseBody
   public ServiceResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
//        service
        ServiceResponse<User> response=iUserService.login(username,password);
        if (response.isSucess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
//            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
////        配置redis连接
//            RedisShardedPoolUtil.setex(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }
    @RequestMapping(value = "login_out.do",method= RequestMethod.POST)
    @ResponseBody
    //    退出用户登录
    public ServiceResponse<User> loginOut(HttpSession session,HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
//        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
//        CookieUtil.delLoginToken(httpServletResponse,httpServletRequest);
//        RedisShardedPoolUtil.del(loginToken);
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    public ServiceResponse<User> getUserInfo(HttpSession session,HttpServletRequest httpServletRequest){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
//        }
//        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.string2Object(userJsonstr,User.class);
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
    }

}
