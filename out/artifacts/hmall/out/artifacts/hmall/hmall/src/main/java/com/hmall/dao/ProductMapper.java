package com.hmall.dao;

import com.hmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectlist();

    List<Product> selectProductByNameAndId(@Param("productName") String productName, @Param("productId")Integer productId);

    List<Product> selectProductByNameAndCategorylists(@Param("productName") String productName, @Param("catagoryLists")List<Integer> catagoryLists);
}