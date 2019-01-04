package com.hmall.controller.backend;


import com.hmall.common.Const;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.service.ICategoryService;
import com.hmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manger/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method= RequestMethod.GET)
    @ResponseBody
        //增加类别
    public ServiceResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentid",defaultValue ="0")int parentid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iCategoryService.addCategory(categoryName,parentid);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
    @RequestMapping(value = "set_category.do",method= RequestMethod.GET)
    @ResponseBody
    //修改类别
    public ServiceResponse setCategory(HttpSession session,Integer categoryid,String categoryName){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iCategoryService.updateCategory(categoryid,categoryName);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "get_category.do",method= RequestMethod.GET)
    @ResponseBody
    //获得平行子节点的类别
    public ServiceResponse getChildrenParalelCategory(HttpSession session,Integer categoryid){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if(iUserService.checkAdmin(user).isSucess()){
//            查询子节点category信息，并且不递归，保持平级
                return iCategoryService.getChildrenParalelCategory(categoryid);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    @RequestMapping(value = "get_deep_category.do",method= RequestMethod.GET)
    @ResponseBody
    public ServiceResponse getDeepChildrenCategory(HttpSession session,Integer categoryid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdmin(user).isSucess()){
//            查询子节点及其子项节点，并进行递归
            return iCategoryService.selectCategoryChrilrenById(categoryid);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
