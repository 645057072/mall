package com.hmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.hmall.common.ResponseCode;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.Product;
import com.hmall.pojo.User;
import com.hmall.service.IFileService;
import com.hmall.service.IProductService;
import com.hmall.service.IUserService;
import com.hmall.unit.CookieUtil;
import com.hmall.unit.JsonUtil;
import com.hmall.unit.PropertieUitl;
import com.hmall.unit.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/manager/product/")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "save_product.do",method= RequestMethod.GET)
    @ResponseBody
    //产品新增和修改
    public ServiceResponse ProductSave(HttpServletRequest httpServletRequest, Product product){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.string2Object(userJsonstr,User.class);
        if (user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录！");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iProductService.saveorupdateByProduct(product);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，请选择使用管理员账户");
      }
    }
    @RequestMapping(value = "set_status_product.do",method= RequestMethod.GET)
    @ResponseBody
    //产品上架处理
    public ServiceResponse setsaleProduct(HttpServletRequest httpServletRequest,Integer productId,Integer status){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return  ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登陆！");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iProductService.seSaleStatus(productId,status);
        }else {
            return ServiceResponse.createByErrorMessage("无权限操作，请选择使用管理员账户");
        }
    }

    @RequestMapping(value = "detail.do",method= RequestMethod.GET)
    @ResponseBody

    //获取产品详细信息
    public ServiceResponse getDetail(HttpServletRequest httpServletRequest,Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iProductService.managerProductDetail(productId);
        }else {
            return ServiceResponse.createByErrorMessage("无权限操作，请选择管理员账户");
        }
    }

    @RequestMapping(value = "list.do",method= RequestMethod.GET)
    @ResponseBody

    //获取产品详细信息
    public ServiceResponse getlist(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iProductService.getProductlist(pageNum,pageSize);
        }else {
            return ServiceResponse.createByErrorMessage("无权限操作，请选择管理员账户");
        }
    }

    @RequestMapping(value = "search.do",method= RequestMethod.GET)
    @ResponseBody
//    查询产品
    public ServiceResponse<PageInfo> getsearchProduct(HttpServletRequest httpServletRequest, String productName, Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServiceResponse.createByErrorMessage("无权限操作，请选择管理员账户");
        }
    }

    @RequestMapping(value = "upload.do",method= RequestMethod.GET)
    @ResponseBody

        public ServiceResponse upload(MultipartFile file, HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessgae(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(iUserService.checkAdmin(user).isSucess()){
            String Path= request.getSession().getServletContext().getRealPath("upload");
            String uploadfilename=iFileService.upload(file,Path);
            String url= PropertieUitl.getProperty("ftp.server.http.prefix")+uploadfilename;

            Map filemap= Maps.newHashMap();
            filemap.put("uri",uploadfilename);
            filemap.put("url",url);
            return ServiceResponse.createBySuccess(filemap);
        }else {
            return ServiceResponse.createByErrorMessage("无权限操作，请选择管理员账户");
        }
    }

    @RequestMapping(value = "richtext_img_upload.do",method= RequestMethod.GET)
    @ResponseBody

    public Map richtextImgupload( MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultmap= Maps.newHashMap();
        String loginToken=CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)){
            resultmap.put("Sucees",false);
            resultmap.put("msg","请登录管理员");
            return resultmap;
        }
        String userJsonstr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Object(userJsonstr,User.class);
        if(user==null){
            resultmap.put("Sucees",false);
            resultmap.put("msg","请登录管理员");
            return resultmap;
        }
//        富文本中上传文件有自己的要求，我们使用的是simditor使用按照simditor要求进行返回
        if(iUserService.checkAdmin(user).isSucess()){
            String Path= request.getSession().getServletContext().getRealPath("upload");
            String uploadfilename=iFileService.upload(file,Path);
            if(StringUtils.isNotBlank(uploadfilename)){
                resultmap.put("Sucees",false);
                resultmap.put("msg","上传成功");
                return resultmap;
            }
            String url= PropertieUitl.getProperty("ftp.server.http.prefix")+uploadfilename;
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            resultmap.put("Sucees",true);
            resultmap.put("url","url");
            return resultmap;
        }else {
            resultmap.put("Sucees",false);
            resultmap.put("msg","无权限操作");
            return resultmap;
        }
    }
}

