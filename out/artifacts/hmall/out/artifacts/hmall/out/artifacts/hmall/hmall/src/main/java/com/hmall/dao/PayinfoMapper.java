package com.hmall.dao;

import com.hmall.pojo.Payinfo;

public interface PayinfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Payinfo record);

    int insertSelective(Payinfo record);

    Payinfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Payinfo record);

    int updateByPrimaryKey(Payinfo record);
}