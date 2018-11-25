package com.edu.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
    //增加用户被关注/喜欢数
    public void addUserLike(String userId);
    //减少用户被关注/喜欢数
    public void reduceUserLike(String userId);
    //增加用户的粉丝数量
    public void addFansCount(String userId);
    //减少用户的粉丝数量
    public void reduceFansCount(String userId);
    //增加用户的关注数量
    public void addFollowCount(String userId);
    //减少用户的关注数量
    public void reduceFollowCount(String userId);

}