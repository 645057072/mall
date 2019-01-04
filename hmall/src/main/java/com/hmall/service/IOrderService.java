package com.hmall.service;


import com.hmall.common.ServiceResponse;

import java.util.Map;

public interface IOrderService {
    ServiceResponse pay(Integer userId, Long orderNo, String path);
    ServiceResponse alicallback(Map<String,String> params);
    ServiceResponse queryOrderPayStatus(Integer userId,Long orderNo);
    ServiceResponse create(Integer userId,Integer shippingId);
}
