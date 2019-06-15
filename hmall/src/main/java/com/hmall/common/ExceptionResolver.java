package com.hmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
      log.error("{} Exception",request.getRequestURI());
      ModelAndView modelAndView=new ModelAndView(new MappingJackson2JsonView());
//      使用jackson2.x时使用MappingJackson2JsonView，本项目使用jaskson1.9
        modelAndView.addObject("status",ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常，详情请查看服务器日志文件");
        modelAndView.addObject("data", ex.toString());
        return modelAndView;
    }
}
