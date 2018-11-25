package com.edu.service.iml;

import com.edu.mapper.*;
import com.edu.service.VideoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class VideoServiceIml implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Override
    public String saveVideo(Videos video) {
        video.setId(sid.nextShort());
        videosMapper.insertSelective(video);
        return video.getId();
    }

    @Override
    public void updataVideo(String videoId, String coverPath) {
        Videos videos=new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        //根据主键id将与之符合的数据中不为空的属性更新
        videosMapper.updateByPrimaryKeySelective(videos);

    }

    @Override
    public PagedResult getAllVideo(Videos videos,Integer isSaveRecords,Integer page, Integer pageSize) {
        //热搜词
        String videoDesc=videos.getVideoDesc();
        //保存热搜词到数据库
        if(isSaveRecords!=null&&isSaveRecords!=0){
            SearchRecords searchRecord=new SearchRecords();
            String recordId=sid.nextShort();
            searchRecord.setId(recordId);
            videoDesc=videos.getVideoDesc();
            searchRecord.setContent(videoDesc);
            searchRecordsMapper.insert(searchRecord);
        }
        PageHelper.startPage(page,pageSize);

        String userId=videos.getUserId();
        //如果热搜词为空或者用户Id为空的话就会返回数据库中所存的所有video
        List<VideosVO>list=videosMapperCustom.queryAllVideos(videoDesc,userId);

        PageInfo<VideosVO>pageInfo=new PageInfo<>(list);

       // System.out.println(pageInfo.getPages()+"---------------------------------");
        PagedResult pagedResult=new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }

    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Override
    public void UserLikeVideo(String videoId, String userId, String videoCreateId) {
        UsersLikeVideos usersLikeVideos=new UsersLikeVideos();
        String id=sid.nextShort();
        usersLikeVideos.setId(id);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        videosMapperCustom.addVideoLike(videoId);

        usersMapper.addUserLike(videoCreateId);
    }

    @Override
    public void UserUnLikeVideo(String videoId, String userId, String videoCreateId) {
        Example example=new Example(UsersLikeVideos.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("videoId",videoId);
        criteria.andEqualTo("userId",userId);
        usersLikeVideosMapper.deleteByExample(example);

        videosMapperCustom.reduceVideoLike(videoId);

        usersMapper.reduceUserLike(videoCreateId);
    }

    @Override
    public PagedResult queryMyCollecVideo(String userId, Integer page, Integer pageSize) {
        //System.out.println(pageSize+"555555555555555555555555");
        PageHelper.startPage(page,pageSize);
        List<VideosVO>list=videosMapperCustom.queryMyCollectVideos(userId);
        PageInfo<VideosVO>pageInfo=new PageInfo<>(list);
        PagedResult pagedResult=new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult queryMyFollowVideo(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideosVO>list=videosMapperCustom.queryMyFollowVideos(userId);
        PageInfo<VideosVO>pageInfo=new PageInfo<>(list);
        PagedResult pagedResult=new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult getAllVideoComments(String videoId, Integer page, Integer pagesize) {
        PageHelper.startPage(page,pagesize);
        List<CommentsVO>list=commentsMapperCustom.queryComments(videoId);
        for(CommentsVO c:list){
            //每次请求都要更新数据里的距离当前时间多长的数据
            c.setTimeAgoStr(TimeAgoUtils.format(c.getCreateTime()));
        }
        PageInfo<CommentsVO>pageInfo=new PageInfo<>(list);
        PagedResult pagedResult=new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        return pagedResult;
    }
}
