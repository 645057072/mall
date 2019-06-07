package com.hmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IOrderService;
import com.hmall.service.IUserService;
import com.hmall.unit.CookieUtil;
import com.hmall.unit.JsonUtil;
import com.hmall.unit.RedisShardedPoolUtil;
import com.hmall.vo.Ordervo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {
    private static final Logger logger= LoggerFactory.getLogger(OrderManagerController.class);
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list")
    @ResponseBody
    public ServiceResponse<PageInfo> orderlist(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1")
            int pageNum, @RequestParam(value = "pageSize",defaultValue = "15") int pageSize){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdmin(user).isSucess()){
            return iOrderService.ManegerList(pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMessage("该用户无权限操作");
        }
    }

    @RequestMapping("detail")
    @ResponseBody
    public ServiceResponse<Ordervo> orderdetail(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if (user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdmin(user).isSucess()){
            return iOrderService.ManagerDetail(orderNo);
        }else{
            return ServiceResponse.createByErrorMessage("该用户无权限操作");
        }
    }

    @RequestMapping("searchorder")
    @ResponseBody
    public ServiceResponse<Ordervo> searchorder(HttpServletRequest httpServletRequest,Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1")
            int pageNum, @RequestParam(value = "pageSize",defaultValue = "15") int pageSize){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if (user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdmin(user).isSucess()){
            return iOrderService.SeachOrder(orderNo,pageNum,pageSize);
        }else{
            return ServiceResponse.createBySuccessMessage("该用户无权限操作");
        }
    }

    @RequestMapping("send_goods")
    @ResponseBody
    public ServiceResponse<String> sendgoods(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if (user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdmin(user).isSucess()){
            return iOrderService.SendGoods(orderNo);
        }
        return ServiceResponse.createBySuccessMessage("该用户无权限操作");
    }
}
