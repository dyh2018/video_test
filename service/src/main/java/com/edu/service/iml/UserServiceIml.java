package com.edu.service.iml;

import com.edu.mapper.*;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.edu.service.UserService;
import com.imooc.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceIml implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private CommentsMapper commentsMapper;



    //查询用户是否存在
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Boolean queryUsernameIsExist(String username) {
        Users user=new Users();
        user.setUsername(username);
        //利用mybatis自带的方法
        Users result=usersMapper.selectOne(user);
        //如果查询到就返回true，反之返回false
        if(result==null){
            return false;
        }
        else{
            return true;
        }
    }
    //查询密码是否正确
    @Override
    public Boolean queryPasswordIsCorrect(String username,String password) {
        Users user=new Users();
        user.setUsername(username);
        Users result=usersMapper.selectOne(user);
        try {
            if(result.getPassword().equals(MD5Utils.getMD5Str(password))){

                return  true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //System.out.println(password);
        return false;
    }

    //保存用户数据
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {

        String userId=sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    //查询数据登录
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example=new Example(Users.class);
        //Criteria 标准
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users users=usersMapper.selectOneByExample(example);
        //失败的话会返回一个空的数据
        return  users;
    }

    //更新用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInformation(Users users) {
        Example example=new Example(Users.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("id",users.getId());
        usersMapper.updateByExampleSelective(users,example);

    }
    //根据id查询用户信息
    @Override
    public Users queryUserInformation(String id) {
        Example example=new Example(Users.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("id",id);
        Users user=usersMapper.selectOneByExample(example);
        return  user;
    }

    @Override
    public Boolean ifUsersLikeVideos(String loginUserId, String videoId) {
        if (StringUtils.isBlank(loginUserId)||StringUtils.isBlank(videoId)){
            return false;
        }
        else{
            Example example=new Example(UsersLikeVideos.class);
            Example.Criteria criteria=example.createCriteria();
            criteria.andEqualTo("userId",loginUserId);
            criteria.andEqualTo("videoId",videoId);
            List<UsersLikeVideos>list=usersLikeVideosMapper.selectByExample(example);
            if(list!=null&&list.size()>0){
                return true;
            }
            else{
                return false;
            }
        }
    }

    @Override
    public void saveUserAndFans(String userId, String fansId) {
        //1.保存用户粉丝关系
        UsersFans usersFans=new UsersFans();
        String id=sid.nextShort();
        usersFans.setId(id);
        usersFans.setUserId(userId);
        usersFans.setFanId(fansId);
        usersFansMapper.insert(usersFans);
        //2.增加关注者的跟随数
        usersMapper.addFollowCount(fansId);
        //3.增加被关注者的粉丝数
        usersMapper.addFansCount(userId);

    }

    @Override
    public void deleteUserAndFans(String userId, String fansId) {
        //1.删除用户粉丝的关系
        Example example=new Example(UsersFans.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fansId);
        usersFansMapper.deleteByExample(example);
        //2.减少关注者的跟随数
        usersMapper.reduceFollowCount(fansId);
        //3.增加被关注者的粉丝数
        usersMapper.reduceFansCount(userId);

    }

    @Override
    public boolean isFans(String userId, String fansId) {
        //1.查找两者是否存在关系
        Example example=new Example(UsersFans.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fansId);
        List<UsersFans>list=usersFansMapper.selectByExample(example);
        if(list!=null&&!list.isEmpty()&&list.size()>0){
            return  true;
        }
        return false;
    }

    @Override
    public void reportVideos(UsersReport usersReport) {
        String id=sid.nextShort();
        usersReport.setId(id);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }

    @Override
    public void saveComment(Comments comments) {
        String id=sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }
}
