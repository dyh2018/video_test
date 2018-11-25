package com.edu.controller;

import com.edu.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController extends BasicController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;


    //这里的userId要和前端的userId保持一致才可以正确传递数据，否则为空！！！！！！
    @PostMapping("/logout")
    public IMoocJSONResult Logout(String userId){
        //传入一个key值!
        redisOperator.del(USER_REDIS_SESSION+":"+userId);
        System.out.println(userId);
        return  IMoocJSONResult.ok();
    }
}
