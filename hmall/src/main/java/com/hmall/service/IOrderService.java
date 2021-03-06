package com.hmall.service;


import com.github.pagehelper.PageInfo;
import com.hmall.common.ServiceResponse;
import com.hmall.vo.Ordervo;

import java.util.Map;

public interface IOrderService {
    ServiceResponse pay(Integer userId, Long orderNo, String path);
    ServiceResponse alicallback(Map<String,String> params);
    ServiceResponse queryOrderPayStatus(Integer userId,Long orderNo);
    ServiceResponse create(Integer userId,Integer shippingId);
    ServiceResponse<String> cancel(Integer userId,Long orderNo);
    ServiceResponse getOrderCartProduct(Integer userId);
    ServiceResponse<Ordervo> getOrderdetail(Integer userId, Long orderNo);
    ServiceResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);


//    backend
    ServiceResponse<PageInfo> ManegerList(int pageNum, int pageSize);
    ServiceResponse<Ordervo> ManagerDetail(Long orderNo);
    ServiceResponse<Ordervo> SeachOrder(Long orderNo,int pageNum, int pageSize);
    ServiceResponse<String> SendGoods(Long orderNo);
}
