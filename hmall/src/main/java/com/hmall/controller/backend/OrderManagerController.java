package com.hmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IOrderService;
import com.hmall.service.IUserService;
import com.hmall.vo.Ordervo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    public ServiceResponse<PageInfo> orderlist(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1")
            int pageNum, @RequestParam(value = "pageSize",defaultValue = "15") int pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
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

    public ServiceResponse<Ordervo> orderdetail(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if (iUserService.checkAdmin(user).isSucess()){
            return iOrderService.ManagerDetail(orderNo);
        }else{
            return ServiceResponse.createByErrorMessage("该用户无权限操作");
        }
    }
}
