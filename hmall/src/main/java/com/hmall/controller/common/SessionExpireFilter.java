package com.hmall.controller.common;

import com.hmall.common.Const;
import com.hmall.pojo.User;
import com.hmall.unit.CookieUtil;
import com.hmall.unit.JsonUtil;
import com.hmall.unit.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
//            查询TOKEN信息不为空
//                    再根据TOKEN转换成json

            String userJsostr= RedisPoolUtil.get(loginToken);
            User user= JsonUtil.string2Object(userJsostr,User.class);
            if (user!=null){
//                用户信息不为空时，设置SESSION的过期时间为30分钟
                RedisPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }

        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
