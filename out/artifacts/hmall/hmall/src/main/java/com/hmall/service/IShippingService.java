package com.hmall.service;

import com.github.pagehelper.PageInfo;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.Shipping;

public interface IShippingService {
    public ServiceResponse add(Integer userId, Shipping shipping);

    ServiceResponse<String> del(Integer userId,Integer shippingId);

    ServiceResponse update(Integer userId,Shipping shipping);

    ServiceResponse<String> seachShipping(Integer userId,Integer shippingId);

    ServiceResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
