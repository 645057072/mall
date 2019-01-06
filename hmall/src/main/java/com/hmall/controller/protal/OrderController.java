package com.hmall.controller.protal;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServiceResponse create(HttpSession session, Integer shippingId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.create(user.getId(),shippingId);
    }
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServiceResponse cancel(HttpSession session, Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServiceResponse getOrderCartProduct(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }



    @RequestMapping("pay.do")
    @ResponseBody
    public ServiceResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            if (user==null){
                return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            String path=request.getSession().getServletContext().getRealPath("upload");

            return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping("callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params= Maps.newHashMap();
        Map requestParams=request.getParameterMap();
        for(Iterator iterator=requestParams.keySet().iterator();iterator.hasNext();){
            String name= (String) iterator.next();
            String[] valuas= (String[]) requestParams.get(name);
            String valuastr="";
            for(int i=0;i<valuas.length-1;i++){
                valuastr=((i==valuas.length-1)?valuastr+valuas[i]:valuastr+valuas[i]+",");
            }
            params.put("name",valuastr);
        }
        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        params.remove("sign");

        try {
            boolean alipayRSACheckedV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheckedV2){
                return ServiceResponse.createByErrorMessage("非法请求，验证不通过，再非法请求就找网警报警了");
            }
        } catch (AlipayApiException e) {
           logger.info("支付宝回调异常",e);
            e.printStackTrace();
        }
        ServiceResponse serviceResponse=iOrderService.alicallback(params);
        if (serviceResponse.isSucess()){
            return Const.alipaycallback.RESPONSE_SUCCESS;
        }
        return Const.alipaycallback.RESPONSE_FAILED;
    }

    @RequestMapping("query_order_pay_status")
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServiceResponse serviceResponse=iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if (serviceResponse.isSucess()){
            return ServiceResponse.createBySuccess(true);
        }
        return ServiceResponse.createBySuccess(false);
    }

}
