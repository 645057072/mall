package com.hmall.controller.protal;

import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IUserService;
import com.hmall.unit.CookieUtil;
import com.hmall.unit.JsonUtil;
import com.hmall.unit.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
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
//            session.setAttribute(Const.CURRENT_USER,response.getData());
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//        配置redis连接
            RedisShardedPoolUtil.setex(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }
    @RequestMapping(value = "login_out.do",method= RequestMethod.POST)
    @ResponseBody
    //    退出用户登录
    public ServiceResponse<User> loginOut(HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletResponse,httpServletRequest);
        RedisShardedPoolUtil.del(loginToken);
//        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }
    @RequestMapping(value = "register.do",method= RequestMethod.POST)
    @ResponseBody
    //用户注册
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }
    @RequestMapping(value = "check_Valid.do",method= RequestMethod.POST)
    @ResponseBody
    //用户校捡
    public ServiceResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping(value = "get_user_info.do",method= RequestMethod.POST)
    @ResponseBody
    //获取用户登录信息
    public ServiceResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if (user!=null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
    }
    @RequestMapping(value = "forget_get_question.do",method= RequestMethod.POST)
    @ResponseBody
    //查找问题答案
    public ServiceResponse<String> forgetquestion(String username){
            return  iUserService.selectquestion(username);
    }
    @RequestMapping(value = "forget_check_answer.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return  iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_Rest_Password.do",method= RequestMethod.POST)
    @ResponseBody
    //重置密码
    public ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
            return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "rest_Password.do",method= RequestMethod.POST)
    @ResponseBody
    //登陆状态修改密码
    public ServiceResponse<String> restPassword(HttpServletRequest httpServletRequest,String passwordOld, String passwordNew){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return  ServiceResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.restPassword(passwordOld,passwordNew,user);
    }
    @RequestMapping(value = "update_info_mation.do",method= RequestMethod.POST)
    @ResponseBody
    //在线修改用户信息
    public ServiceResponse<User> updateInfoMation(HttpServletRequest httpServletRequest,User user){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.string2Object(userJsonstr,User.class);
        if(currentUser==null){
            return  ServiceResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        ServiceResponse<User> serviceResponse= iUserService.updateInfoMation(user);
        if(serviceResponse.isSucess()){
            serviceResponse.getData().setUsername(currentUser.getUsername());
            RedisShardedPoolUtil.expire(loginToken,Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return serviceResponse;
    }
    @RequestMapping(value = "get_info_mation.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getInfoMation(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.string2Object(userJsonstr,User.class);
        if(currentUser==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"未登陆需要强制登陆status=10");
        }
        return iUserService.getInfomation(currentUser.getId());
    }
}
