package com.douyin.service.intercepter;

import com.douyin.base.BaseInfoProperties;
import com.douyin.service.exceptions.GraceException;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserTokenInterceptor extends BaseInfoProperties implements HandlerInterceptor {


    /*
    * controller访问请求之前会被拦截到
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.首先获取用户的id 和token, 注意参数要和前端一一对应
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        System.out.println(userToken);

        // 2. 判断用户中的id和token不能为空
        if (!StringUtils.isBlank(userId) && !StringUtils.isBlank(userToken)){
            String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisToken)){
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            }else {
                // 比较token是否一致，如果不一致表示用户在别的手机端登录
                if (!redisToken.equalsIgnoreCase(userToken)){
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return  false;
                }
            }
        }else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
        }
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
