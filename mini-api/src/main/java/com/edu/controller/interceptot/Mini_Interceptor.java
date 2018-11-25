package com.edu.controller.interceptot;

import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Mini_Interceptor implements HandlerInterceptor {

    @Autowired
    RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION="user-redis-session";

    //在controller之前
    //false代表拦截 true代表接受
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String userToken=httpServletRequest.getHeader("userToken");
        String userId=httpServletRequest.getHeader("userId");

        if(StringUtils.isBlank(userId)||StringUtils.isBlank(userToken)){
            //System.out.println(userId+"-----------"+userToken);
            returnErrorResponse(httpServletResponse,new IMoocJSONResult().errorTokenMsg("请登录1"));
            System.out.println("请登录1");
            return  false;
        }else {
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION +":"+ userId);
            //微信端保存的本地缓存的userToken是存在的，但是redis的userToken可能已经过期了！！
            if (StringUtils.isBlank(uniqueToken) || StringUtils.isEmpty(uniqueToken)) {
                returnErrorResponse(httpServletResponse,new IMoocJSONResult().errorTokenMsg("请登录2"));
                System.out.println("请登录2");
                return false;
            } else {
                //只允许用户在一台手机上登录
                if (!uniqueToken.equals(userToken)) {
                    returnErrorResponse(httpServletResponse,new IMoocJSONResult().errorTokenMsg("被挤出3"));
                    System.out.println("被挤出了3");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            //转换成字节数组，指定以UTF-8编码进行转换
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }





    //在controller之后，渲染视图之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    //渲染视图之后
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
