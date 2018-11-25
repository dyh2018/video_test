package com.edu.service;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

public interface VideoService {

    public String saveVideo(Videos video);

    //根据videoId和coverPath来更新数据中的视频封面的相对路径
    public void updataVideo(String videoId,String coverPath);
    //根据page和pageSize来分页查找视频列表
    public PagedResult getAllVideo(Videos videos,Integer isSaveRecords,Integer page,Integer pageSize);
    //得到热搜词
    public List<String> getHotWords();
    //用户喜欢一个视频
    public void UserLikeVideo(String videoId,String userId,String videoCreateId);
    //用户不喜欢一个视频
    public void UserUnLikeVideo(String videoId,String userId,String videoCreateId);
    //查找我收藏的视频列表
    public PagedResult queryMyCollecVideo(String userId,Integer page,Integer pageSize);
    //查找我关注的人的视频列表
    public PagedResult queryMyFollowVideo(String userId,Integer page,Integer pageSize);
    //获取用户对视频的评论列表
    public PagedResult getAllVideoComments(String videoId,Integer page,Integer pagesize);

}
