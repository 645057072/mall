package com.hmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.CategoryMapper;
import com.hmall.pojo.Category;
import com.hmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService {
    private Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ServiceResponse addCategory(String categoryName,Integer parentid){
        if (parentid==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("参数错误，未或获取正确参数");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setId(parentid);

        category.setStatus(true);
        int rowCount=categoryMapper.insert(category);
        if (rowCount>0){
            return ServiceResponse.createBySuccessMessage("添加类别成功");
        }
        return ServiceResponse.createByErrorMessage("添加类别失败");
    }

    public ServiceResponse updateCategory(Integer categoryid,String categoryName){
        if (categoryid==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("参数错误，未或获取正确参数");
        }
        Category category=new Category();
        category.setId(categoryid);
        category.setName(categoryName);

        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);

        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("修改类别成功");
        }
        return ServiceResponse.createByErrorMessage("修改类别失败");
    }

    public ServiceResponse<List<Category>> getChildrenParalelCategory(Integer categoryid){
        List<Category> categoriesList=categoryMapper.selectCategoryChildrenByParentid(categoryid);
        if (CollectionUtils.isEmpty(categoriesList)){
            logger.info("未找到当前分类的子类");
        }
        return ServiceResponse.createBySuccess(categoriesList);
    }

    public ServiceResponse<List<Integer> > selectCategoryChrilrenById(Integer categoryid){
        Set<Category> categorySet= Sets.newHashSet();
        findaChildrenCategory(categorySet,categoryid);

        List<Integer> categoryidList= Lists.newArrayList();
        if (categoryid!=null){
            for (Category categoryItem:categorySet){
                categoryidList.add(categoryItem.getId());
            }
        }
        return ServiceResponse.createBySuccess(categoryidList);
    }
//        递归算法，算出子节点
    private  Set<Category> findaChildrenCategory(Set<Category> categorySet, Integer categoryid){
            Category category= (Category) categoryMapper.selectByPrimaryKey(categoryid);
            if(category!=null){
                categorySet.add(category);
        }
        //查找子节点，递归算法一定需要退出条件
        List<Category> categories=categoryMapper.selectCategoryChildrenByParentid(categoryid);
            for(Category categoryItem:categories){
                findaChildrenCategory(categorySet,categoryItem.getId());
            }
            return categorySet;
    }
}
