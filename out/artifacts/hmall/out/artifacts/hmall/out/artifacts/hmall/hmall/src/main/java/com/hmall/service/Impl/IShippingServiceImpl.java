package com.hmall.service.Impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.ShippingMapper;
import com.hmall.pojo.Shipping;
import com.hmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class IShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;
    public ServiceResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowResult=shippingMapper.insert(shipping);
        if(rowResult>0){
            Map result= Maps.newHashMap();
            result.put("shippingid",shipping.getId());
            return ServiceResponse.createBySuccess("新增地址成功",result);
        }
        return ServiceResponse.createByErrorMessage("新增地址失败");
    }

    public ServiceResponse<String> del(Integer userId,Integer shippingId){
        int resultCount=shippingMapper.deletShippingByUserIdShippingId(userId,shippingId);
        if(resultCount>0){
            return ServiceResponse.createBySuccess("删除地址成功");
        }
        return ServiceResponse.createByErrorMessage("删除地址失败");
    }

    public ServiceResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int resultCount=shippingMapper.updateByShipping(shipping);
        if(resultCount>0){
            return ServiceResponse.createBySuccess("修改地址成功");
        }
        return ServiceResponse.createByErrorMessage("修改地址失败");
    }

    public ServiceResponse<String> seachShipping(Integer userId,Integer shippingId){
            Shipping shipping=shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping==null){
            return ServiceResponse.createByErrorMessage("查询失败");
        }
        return ServiceResponse.createBySuccessMessage("查询成功");
    }

    public ServiceResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServiceResponse.createBySuccess(pageInfo);

    }
}
