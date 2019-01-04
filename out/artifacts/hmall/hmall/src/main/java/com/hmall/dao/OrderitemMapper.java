package com.hmall.dao;

import com.hmall.pojo.Orderitem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderitemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Orderitem record);

    int insertSelective(Orderitem record);

    Orderitem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orderitem record);

    int updateByPrimaryKey(Orderitem record);

    List<Orderitem> getByOrderNoUserId(@Param("orderNo") Long orderNo, @Param("userId")Integer userId);
}