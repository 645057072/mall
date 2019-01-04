package com.hmall.controller.protal;


import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.ICartService;
import com.hmall.vo.Cartvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse<Cartvo> add(HttpSession session ,Integer count,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.add(user.getId(),count,productId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse<Cartvo> update(HttpSession session, Integer count, Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.update(user.getId(),count,productId);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServiceResponse<Cartvo> delete(HttpSession session,String productIds){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
         return iCartService.delete(user.getId(),productIds);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<Cartvo> list(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServiceResponse<Cartvo> selectAll(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServiceResponse<Cartvo> unSelectAll(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<Cartvo> select(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServiceResponse<Cartvo> unSelect(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
            return iCartService.getCountProductCount(user.getId());
    }
}
