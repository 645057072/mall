package com.hmall.dao;

import com.hmall.pojo.Cart;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@RequestParam("userId") Integer userId,@RequestParam("productId") Integer productId);

    List<Cart> selectCartByuserId(Integer userId);

    int selectAllCheckedStatusByUserId(Integer userId);

    int  deleteByuserIdProductIds(@RequestParam("userId") Integer userId,@RequestParam("productList") List<String> productList);

    int checkedOrUncheckedproductId(@RequestParam("userId") Integer userId,@RequestParam("checked") Integer checked);

    int checkedOrUncheckedproductId(@RequestParam("userId")Integer userId, @RequestParam("productId")Integer productId, @RequestParam("checked")Integer checked);

    int getCartProductCount(Integer userId);

    List<Cart> selectByCheckedUserId(Integer userId);
}