package com.hmall.service;

import com.github.pagehelper.PageInfo;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.Product;
import com.hmall.vo.ProductDetailvo;

public interface IProductService {
    ServiceResponse saveorupdateByProduct(Product product);

    ServiceResponse seSaleStatus(Integer productId,Integer status);

    ServiceResponse<ProductDetailvo> managerProductDetail(Integer productId);

    ServiceResponse<PageInfo> getProductlist(int pageNum, int pageSize);

    ServiceResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    ServiceResponse<ProductDetailvo> detial(Integer productId);

    ServiceResponse<PageInfo> list(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);

}
