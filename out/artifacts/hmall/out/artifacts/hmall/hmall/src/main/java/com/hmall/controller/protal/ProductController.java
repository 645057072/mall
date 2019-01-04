package com.hmall.controller.protal;


import com.github.pagehelper.PageInfo;
import com.hmall.common.ServiceResponse;
import com.hmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServiceResponse detail(Integer productId){
        return iProductService.detial(productId);
    }

    public ServiceResponse<PageInfo> list(@RequestParam(value = "keyword" ,required = false) String keyword,
                                          @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                          @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                          @RequestParam(value="",defaultValue = "") String orderBy){
        return iProductService.list(keyword,categoryId,pageNum,pageSize,orderBy);

    }
}
