package com.hmall.service;

import com.hmall.common.ServiceResponse;
import com.hmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServiceResponse addCategory(String categoryName, Integer parentid);

    ServiceResponse updateCategory(Integer categoryid,String categoryName);

    ServiceResponse<List<Category>> getChildrenParalelCategory(Integer categoryid);

    ServiceResponse<List<Integer>> selectCategoryChrilrenById(Integer categoryid);


}
