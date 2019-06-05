package com.hmall.unit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class CookieUtil {

    private static final String COOKIE_DOMAIN=".hlx.com";
    private static final String COOKIE_NAME="hmall_login_token";
//读取session信息中cookie
    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies=request.getCookies();
        if (cookies!=null){
            for (Cookie cookie:cookies){
                log.info("read cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
                if (StringUtils.equals(cookie.getName(),COOKIE_DOMAIN)){
                    log.info("return cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
//    写入session信息中cookie
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie=new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);//防止使用脚本攻击
//        单位秒
//        如果不写时间话，代表不会写入硬盘，只存在内存中
       cookie.setMaxAge(60*60*24*365);// 如果是-1代表永久
        log.info("write cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }
//删除读取到的session信息中cookie
    public static void delLoginToken(HttpServletResponse response,HttpServletRequest request){
        Cookie[] cookies=request.getCookies();
        if (cookies!=null){
            for (Cookie cookie:cookies){
                if (StringUtils.equals(cookie.getName(),COOKIE_DOMAIN)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    log.info("delete cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                }
            }
        }
    }
}
