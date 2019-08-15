package com.hmall.controller.interceptor;

import com.github.pagehelper.StringUtil;
import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;
import com.hmall.unit.CookieUtil;
import com.hmall.unit.JsonUtil;
import com.hmall.unit.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        //请求中controller的方法名
        HandlerMethod handlerMethod=(HandlerMethod)handler;
//        解析handlerMethod
        String methodName=handlerMethod.getMethod().getName();
        String className=handlerMethod.getBean().getClass().getSimpleName();
//        解析参数，获取KEY和Value是什么，打印日志
        StringBuffer requestParamBuffer=new StringBuffer();
        Map paramMap=request.getParameterMap();
//        使用迭代器（Iterator)或Map中的值
        Iterator iterator=paramMap.entrySet().iterator();
        while (iterator.hasNext()){
//           将下一个参数转化成entry
            Map.Entry entry=(Map.Entry)iterator.next();
            String mapKey=(String)entry.getKey();
            String mapValue= StringUtils.EMPTY;
//            request这个参数的may，value返回的一个数组String[]
            Object object=entry.getValue();
            if (object instanceof String[]){
                String[] strings= (String[]) object;
                mapValue= Arrays.toString(strings);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);

            if (StringUtils.equals(className,"UserController")&&StringUtils.equals(methodName,"login")){
                log.info("连接器拦截到请求参数，classNmae:{},methodName:{}",className,methodName);
                return true;
            }


            User user=null;
            String loginToken= CookieUtil.readLoginToken(request);
            if (StringUtil.isNotEmpty(loginToken)){
                String userJsonstr= RedisShardedPoolUtil.get(loginToken);
                user= JsonUtil.string2Object(userJsonstr,User.class);
            }
            if (user==null||(user.getRole().intValue()!= Const.Role.ROLE_ADMIN)){
//                返回false，则不会调用controller
                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");

                PrintWriter out=response.getWriter();
                if (user==null){
                    out.print(JsonUtil.obj2String(ServiceResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }else {
                    out.print(JsonUtil.obj2String(ServiceResponse.createByErrorMessage("拦截器拦截，用户不是管理员，请使用管理员权限")));
                }
                out.flush();
                out.close();
            }
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}
