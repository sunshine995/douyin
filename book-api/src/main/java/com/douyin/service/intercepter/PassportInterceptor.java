package com.douyin.service.intercepter;


import com.douyin.base.BaseInfoProperties;
import com.douyin.service.exceptions.GraceException;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class PassportInterceptor extends BaseInfoProperties implements HandlerInterceptor {


    /*
    * controller访问请求之前会被拦截到
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 判断这个key是否存在
        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);
        if (keyIsExist){
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        /**
         * true ： 请求放行
         * false ： 禁止通行
         */

        return true;
    }
    /*
    * 在访问controller之后，访问视图层之前会被渲染
    * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
    /*
     * 在访问视图之后
     * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
