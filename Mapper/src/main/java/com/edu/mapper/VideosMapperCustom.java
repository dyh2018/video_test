package com.edu.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
	//  条件查询所有视频列表
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
										 @Param("userId") String userId
	);
	//增加视频喜欢数量
	public  void addVideoLike(String videoId);
	//减少视频喜欢数量
	public  void reduceVideoLike(String videoId);
	//查询我收藏的视频
	public List<VideosVO> queryMyCollectVideos(String userId);
	//查询我关注人的视频
	public List<VideosVO> queryMyFollowVideos(String userId);

}