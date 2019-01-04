package com.hmall.service;


import com.hmall.common.ServiceResponse;
import com.hmall.vo.Cartvo;

public interface ICartService {
    ServiceResponse<Cartvo> add(Integer userId, Integer productId, Integer count);

    ServiceResponse<Cartvo> update(Integer userId,Integer productId,Integer count);

    ServiceResponse<Cartvo> delete(Integer userId,String productIds);

    ServiceResponse<Cartvo> list(Integer userId);

    ServiceResponse<Cartvo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);

    ServiceResponse<Integer> getCountProductCount(Integer userId);
}
