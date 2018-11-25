package com.edu.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redisOperator;
    //idea 大小写转换 ctrl+shift+u
    public static final String USER_REDIS_SESSION="user-redis-session";
    public  static final  String FFMPEGEXE="D:\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe";
    public  static  final  String FILESPACE="D:/idea_java_project/UserData";
    //每页分多少个video
    public  static  final  Integer PAGESIZE=3;

}
