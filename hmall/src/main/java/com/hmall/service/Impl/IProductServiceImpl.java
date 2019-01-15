package com.hmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.CategoryMapper;
import com.hmall.dao.ProductMapper;
import com.hmall.pojo.Category;
import com.hmall.pojo.Product;
import com.hmall.service.ICategoryService;
import com.hmall.service.IProductService;
import com.hmall.unit.DateTimeUtil;
import com.hmall.unit.PropertieUitl;
import com.hmall.vo.ProductDetailvo;
import com.hmall.vo.ProductListvo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Service("iProductService")
public class IProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Override
    public ServiceResponse saveorupdateByProduct(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImagesArray = product.getSubImages().split(",");
                if (subImagesArray.length>0){
                    product.setSubImages(subImagesArray[0]);
                }
            }
            if(product.getId()!=null){
                productMapper.updateByPrimaryKey(product);
                return ServiceResponse.createBySuccessMessage("更新产品信息成功！");
            }else {
                productMapper.insert(product);
                return ServiceResponse.createBySuccessMessage("新增产品信息成功");
            }
        }
        return ServiceResponse.createByErrorMessage("新增或修改产品信息不正确");
    }

    public ServiceResponse seSaleStatus(Integer productId,Integer status){
        if(productId==null||status==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount=productMapper.updateByPrimaryKey(product);
        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServiceResponse.createByErrorMessage("修改产品状态失败！");
    }

    public ServiceResponse<ProductDetailvo> managerProductDetail(Integer productId){
        if (productId==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServiceResponse.createBySuccessMessage("产品已下架或者删除");
        }
        ProductDetailvo productDetailvo=assmbleProductDetailvo(product);
        return ServiceResponse.createBySuccess(productDetailvo);
    }

    private ProductDetailvo assmbleProductDetailvo(Product product){
        ProductDetailvo productDetailvo=new ProductDetailvo();
        productDetailvo.setId(product.getId());
        productDetailvo.setName(product.getName());
        productDetailvo.setSubtitle(product.getSubtitle());
        productDetailvo.setMainimage(product.getMainImage());
        productDetailvo.setSubimages(product.getSubImages());
        productDetailvo.setDetail(product.getDetail());
        productDetailvo.setPrice(product.getPrice());
        productDetailvo.setStock(product.getStock());
        productDetailvo.setStatus(product.getStatus());

        productDetailvo.setImageHost(PropertieUitl.getProperty("ftp.server.http.prefix","http://img.hmall.com"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
          productDetailvo.setParentCategoryId(0);
        }else {
            productDetailvo.setParentCategoryId(product.getCategoryId());
        }

        productDetailvo.setCreatetime(DateTimeUtil.dateTostr(product.getCreateTime()));
        productDetailvo.setUpdatetime(DateTimeUtil.dateTostr(product.getUpdateTime()));

        return productDetailvo;
    }

    public ServiceResponse<PageInfo> getProductlist(int pageNum,int pageSize){
        //startPage-----start
        //启动查询逻辑
        //endPage=end
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectlist();
        List<ProductListvo> productListvoList=Lists.newArrayList();
        for(Product productItem:productList){
            ProductListvo productListvo=assmbleProductlistvo(productItem);
            productListvoList.add(productListvo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListvoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    public ProductListvo assmbleProductlistvo(Product product){
        ProductListvo productListvo=new ProductListvo();
        productListvo.setId(product.getId());
        productListvo.setCategoryId(product.getCategoryId());
        productListvo.setName(product.getName());
        productListvo.setSubtitle(product.getSubtitle());
        productListvo.setMainimage(product.getMainImage());
        productListvo.setPrice(product.getPrice());
        productListvo.setStatus(product.getStatus());
        productListvo.setImageHost(PropertieUitl.getProperty("ftp.server.http.prefix","http://img.hmall.com"));
        return productListvo;
    }

    public ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "15") int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNoneBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList=productMapper.selectProductByNameAndId(productName,productId);
        List<ProductListvo> productListvoList=Lists.newArrayList();
        for(Product productItem:productList){
            ProductListvo productListvo=assmbleProductlistvo(productItem);
            productListvoList.add(productListvo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListvoList);
        return  ServiceResponse.createBySuccess(pageResult);
    }

    public ServiceResponse<ProductDetailvo> detial(Integer productId){
        if(productId==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServiceResponse.createByErrorMessage("产品已下架或者删除");
        }
        if (product.getStatus()!= Const.ProductStutasenum.ON_SALES.getCode()){
            return ServiceResponse.createByErrorMessage("产品已下架或者删除");

        }
        ProductDetailvo productDetailvo=assmbleProductDetailvo(product);
        return ServiceResponse.createBySuccess(productDetailvo);
    }


    public ServiceResponse<PageInfo> list(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword)&&categoryId==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.ILLEGAL_AGRUMENT.getCode(),ResponseCode.ILLEGAL_AGRUMENT.getDesc());
        }
        List<Integer> categoryIdList=new ArrayList<Integer>();
        if(categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&StringUtils.isBlank(keyword)){
//                没有该分类，并且没有关键字，返回一个空集合，并且不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListvo> productListvoList= Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListvoList);
                return ServiceResponse.createBySuccess(pageInfo);
            }
           categoryIdList=iCategoryService.selectCategoryChrilrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductlistOrderby.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderbyArray=orderBy.split("_");
                PageHelper.orderBy(orderbyArray[0]+" "+orderbyArray[1]);
            }
        }
        List<Product> productList=productMapper.selectProductByNameAndCategorylists(StringUtils.isNotBlank(keyword)?null:keyword,
                categoryIdList.size()==0?null:categoryIdList);
        List<ProductListvo> productListvoList=Lists.newArrayList();
        for(Product productItem:productList){
            ProductListvo productListvo=assmbleProductlistvo(productItem);
            productListvoList.add(productListvo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListvoList);

        return  ServiceResponse.createBySuccess(pageResult);
    }
}
