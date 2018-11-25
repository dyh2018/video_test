package com.edu.service;


import com.imooc.pojo.Comments;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

public interface UserService {

    //判断用户名是否存在
    public  Boolean queryUsernameIsExist(String username);
    //判断用户的密码是否正确
    public  Boolean queryPasswordIsCorrect(String username,String password);
    //保存用户数据
    public void saveUser(Users users);
    //用户登录，根据用户名和密码查询用户
    public  Users queryUserForLogin(String username,String password);
    //更新用户信息
    public void updateUserInformation(Users users);
    //根据id查询用户信息
    public  Users queryUserInformation(String id);
    //判断登录用户与视频是否存在点赞/喜欢关系
    public Boolean ifUsersLikeVideos(String loginUserId,String videoId);

    //保存关注者和被关注者的关系，使关注者的FOLLOW_COUNT增加，被关注者的FANS_COUNT增加
    public  void saveUserAndFans(String userId,String fansId);
    //删除关注者和被关注者的关系，使关注者的FOLLOW_COUNT减少，被关注者的FANS_COUNT减少
    public  void deleteUserAndFans(String userId,String fansId);

    //判断用户和视频发布者是否是粉丝关系
    public  boolean isFans(String userId,String fansId);
    //举报视频
    public void reportVideos(UsersReport usersReport);
    //保存用户对视频的评论
    public void saveComment(Comments comments);



}
