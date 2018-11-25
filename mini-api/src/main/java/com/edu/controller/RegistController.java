package com.edu.controller;

import com.edu.service.UserService;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "用户注册和登录的接口",tags = {"用户注册和登录的controller"})
public class RegistController extends BasicController {
    @Autowired
    private UserService userService;

    //@RequestBody注解用于读取http请求的内容(字符串)，
    // 通过springmvc提供的HttpMessageConverter接口将读到的内容（json数据）转换为java对象并绑定到Controller方法的参数上。
    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult  regist (@RequestBody Users user) throws Exception {
        //1.判断用户名和密码不为空
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            //二者之一或二者都为空
            return  IMoocJSONResult.errorMsg("用户名或密码不应为空");
        }

        //2.判断用户名是否存在
        boolean usernameIsExist=userService.queryUsernameIsExist(user.getUsername());
        //3.保存用户信息
        if(!usernameIsExist){
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setFollowCounts(0);
            user.setReceiveLikeCounts(0);
            userService.saveUser(user);
        }
        else{
            return IMoocJSONResult.errorMsg("用户已经存在了");
        }
        //为了安全，因为上述已经注册成功了，所以把密码设置为空！！！
        user.setPassword("");
        UsersVO usersVO=setRedisToken(user);
        //返回的是usersVO
        return IMoocJSONResult.ok(usersVO);
    }

    //添加UserToken
    public  UsersVO setRedisToken(Users users)
    {
        //UUID.randomUUID().toString()是javaJDK提供的一个自动生成主键的方法,也就是生成唯一的字符串
        String uniqueToken=UUID.randomUUID().toString();
        //timeout单位为毫秒 1000*60*30=半小时
        //使用"："为了在最后redis管理软件显示清楚
        redisOperator.set(USER_REDIS_SESSION+":"+users.getId(),uniqueToken,1000*60*300);
        UsersVO usersVO=new UsersVO();
        //使用BeanUtils.copyProperties来进行数据属性的拷贝！！！！
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }
}
