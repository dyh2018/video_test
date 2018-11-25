package com.edu.controller;

import com.edu.service.UserService;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LoginController  extends BasicController {
    @Autowired
    private UserService userService;
    //@RequestBody接受的是一个json格式的字符串，一定是一个字符串。
    @PostMapping("/login")
    public IMoocJSONResult Login(@RequestBody Users user)throws Exception{
//        //1.判断用户名不存在
//        if(!userService.queryUsernameIsExist(user.getUsername())){
//            return  IMoocJSONResult.errorMsg("用户名不存在，靴靴！");
//        }
//        else if(userService.queryPasswordIsCorrect(user.getUsername(),user.getPassword())==false) {
//            return  IMoocJSONResult.errorMsg("烧鸡，密码不正确啊！");
//        }
//        else{
//            //为了安全，因为上述已经登录成功了，所以把密码设置为空！！！
//            user.setPassword("");
//            UsersVO usersVO=setRedisToken(user);
//            //返回的是usersVO
//            return  IMoocJSONResult.ok(usersVO);
//        }

        String username=user.getUsername();
        String password=user.getPassword();

        Users result=userService.queryUserForLogin(username,MD5Utils.getMD5Str(password));
        if(result!=null){
            //为了安全设置密码为空再返回
            result.setPassword("");
            UsersVO usersVO=setRedisToken(result);
            return  IMoocJSONResult.ok(usersVO);
        }
        else{
            return IMoocJSONResult.errorMsg("用户名或密码错误！！");
        }

    }
    //添加UserToken
    public UsersVO setRedisToken(Users users)
    {
        //UUID.randomUUID().toString()是javaJDK提供的一个自动生成主键的方法,也就是生成唯一的字符串
        String uniqueToken=UUID.randomUUID().toString();
        //timeout单位为毫秒 1000*60*30=半小时
        int time=0;
        //使用"："为了在最后redis管理软件显示清楚
        redisOperator.set(USER_REDIS_SESSION+":"+users.getId(),uniqueToken,1000*60*300);
        UsersVO usersVO=new UsersVO();
        //使用BeanUtils.copyProperties来进行数据属性的拷贝！！！！
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }
}
