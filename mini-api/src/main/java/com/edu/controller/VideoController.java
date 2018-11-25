package com.edu.controller;

import com.edu.compoundBgmVideo;
import com.edu.getVideoCover;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Videos;
import com.edu.service.BgmService;
import com.edu.service.VideoService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.PagedResult;
import com.imooc.utils.enums.VideoStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/video")
@Api(value="视频相关业务的接口", tags= {"视频相关业务的controller"})
public class VideoController extends  BasicController {

    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;

    //上传文件
    @ApiOperation(value="上传视频", notes="上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户id", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="bgmId", value="背景音乐id", required=false,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoWidth", value="视频宽度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoHeight", value="视频高度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="desc", value="视频描述", required=false,
                    dataType="String", paramType="form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadFace(String userId, String bgmId,double videoSeconds,int videoWidth,int videoHeight,
                      String desc ,  MultipartFile file) throws IOException {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace = FILESPACE;
        //保存到数据库里原始视频的相对路径
        String uploadPathDB = "/" + userId + "/video";
        //保存到数据库里的封面图片的相对路径
        String coverPathDB="/"+userId+"/cover";
        //写入文件(视频）
        FileOutputStream fileOutputStream = null;
        //读取文件（视频）
        InputStream inputStream = null;
        //写入文件（封面图片）
        FileInputStream fileInputStream_CP=null;
        //读取文件（封面图片）
        FileOutputStream fileOutputStream_CP=null;

        String finalCoverPath="";

        String finalVideoPath="";
        //如果文件存在且长度大于0
        try {
            if (file != null ) {
                //得到文件名 XX.MP4
                String fileName = file.getOriginalFilename();
                //如果文件名不为空
                if (StringUtils.isNotBlank(fileName)) {
                    //取文件名前缀 xx.mp4 取xx
                    String fileNamePre=fileName.split("\\.")[0];
                    //原始视频文件上传保存的最终路径
                    finalVideoPath = fileSpace + uploadPathDB + "/" + fileName;
                    //封面图片的最终路径
                    finalCoverPath=fileSpace+coverPathDB+"/"+fileNamePre+".jpg";
                    //设置原始视频数据库保存路径
                    uploadPathDB+= ("/" + fileName);
                    //设置封面图片数据库保存路径
                    coverPathDB+=("/"+fileNamePre+".jpg");

                    File outFile = new File(finalVideoPath);
                    //isDirectory（）如果是目录文件返回true，否则返回false
                    //如果存在父路径或者没有父路径
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                       // mkdirs()创建多层目录
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    //通过ioutil 对接输入输出流，实现文件下载
                   IOUtils.copy(inputStream, fileOutputStream);

                   File outFile_cp=new File(finalCoverPath);
                   if(outFile_cp.getParentFile()!=null||!outFile_cp.getParentFile().isDirectory()){
                       outFile_cp.getParentFile().mkdirs();
                   }
                   //fileOutputStream_CP=new FileOutputStream(outFile_cp);
                   //IOUtils.copy(inputStream,fileOutputStream_CP);
                }
            }
            else{
                return IMoocJSONResult.errorMsg("文件名为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if(fileOutputStream_CP!=null){
                fileOutputStream_CP.flush();
                fileOutputStream_CP.close();
            }
        }
        //截取视频的第1秒的一帧作为封面图片
        getVideoCover getVideoCover=new getVideoCover(FFMPEGEXE);
        getVideoCover.convertor(finalVideoPath,FILESPACE+coverPathDB);


        String uploadVideoPath="";
        //如果有bgmId则进行bgm与视频的合并！！！
        if(StringUtils.isNotBlank(bgmId)){
            Bgm bgm=bgmService.queryBgmBybgmId(bgmId);
            String mp3inputPath=FILESPACE+bgm.getPath();
            String videoinputPath= finalVideoPath;
            uploadVideoPath="/"+userId+"/video/"+UUID.randomUUID().toString()+".mp4";
            String videooutPath=FILESPACE+uploadVideoPath;
            compoundBgmVideo compoundBgmVideo =new compoundBgmVideo(FFMPEGEXE);
            compoundBgmVideo.convertor(videoinputPath,mp3inputPath,videoSeconds,videooutPath);
        }
        else{
            uploadVideoPath=uploadPathDB;
        }
        //保存视频信息到数据库
        Videos video=new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoDesc(desc);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoSeconds((float)videoSeconds);
        video.setCoverPath(coverPathDB);
        video.setVideoPath(uploadVideoPath);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());
        String videoId=videoService.saveVideo(video);
        //返回videoId
        return IMoocJSONResult.ok(videoId);
    }
    //查询视频
    //isSaveRecords:1 需要保存热搜词
    //              0或者为空 不需要保存热搜词
    @PostMapping(value = "/showAll")
    public  IMoocJSONResult showAll(@RequestBody Videos videos , Integer isSaveRecords,Integer page,Integer pagesize){
        if(page==0){
            page=1;
        }
        if(isSaveRecords==null){
            isSaveRecords=0;
        }
        PagedResult pagedResult=videoService.getAllVideo(videos,isSaveRecords,page,pagesize);
        return  IMoocJSONResult.ok(pagedResult);
    }
    @PostMapping(value = "/hot")
    public  IMoocJSONResult getHotWords(){
        return  IMoocJSONResult.ok(videoService.getHotWords());
    }


    //videoCreateId视频创建者
    //用户喜欢该视频
    @PostMapping(value = "/userLike")
    public  IMoocJSONResult Like(String videoId, String userId, String videoCreateId){
        videoService.UserLikeVideo(videoId,userId,videoCreateId);
        return  IMoocJSONResult.ok();
    }
    //用户不喜欢该视频
    @PostMapping(value = "/userUnLike")
    public  IMoocJSONResult UnLike(String videoId, String userId, String videoCreateId){
        videoService.UserUnLikeVideo(videoId,userId,videoCreateId);
        return  IMoocJSONResult.ok();
    }

    //查找我收藏的视频
    @PostMapping(value = "/showmycollectvideos")
    public IMoocJSONResult showMyCollectVideos(String userId,Integer page,Integer pagesize){
        if(page==0){
            page=1;
        }
        PagedResult pagedResult=videoService.queryMyCollecVideo(userId,page,pagesize);
        return  IMoocJSONResult.ok(pagedResult);
    }
    //查找我关注的人的视频列表
    @PostMapping(value = "/showmyfollowvideos")
    public IMoocJSONResult showMyFollowVideos(String userId,Integer page,Integer pagesize){
        if(page==0){
            page=1;
        }
        PagedResult pagedResult=videoService.queryMyFollowVideo(userId,page,pagesize);
        return IMoocJSONResult.ok(pagedResult);
    }

    //得到用户对视频的全部评论
    @PostMapping(value = "/getvideocomments")
    public IMoocJSONResult getAllVideoComments(String videoId,Integer page,Integer pagesize){
        if (StringUtils.isBlank(videoId)){
            return IMoocJSONResult.errorMsg("vidoeId为空");
        }
        if(page==null){
            page=1;
        }
        if(pagesize==null){
            pagesize=10;
        }
        PagedResult pagedResult=videoService.getAllVideoComments(videoId,page,pagesize);
        return  IMoocJSONResult.ok(pagedResult);
    }





























    //更新视频图片封面 因为微信手机端有bug所以下面代码作废
    @PostMapping(value = "/uploadCoverPath",headers = "content-type=multipart/form-data")
    public IMoocJSONResult updataCoverPath(String userId,String videoId,MultipartFile file)throws Exception{
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return  IMoocJSONResult.errorMsg("错误");
        }
        //文件保存的命名空间
        String fileSpace = "D:/idea_java_project/UserData";
        //保存到数据库里的相对路径
        String uploadPathDB = "/" + userId + "/video";
        //写入文件
        FileOutputStream fileOutputStream = null;
        //读取文件
        InputStream inputStream = null;
        String finalCoverPath="";
        //如果文件存在且长度大于0
        try {
            if (file != null ) {
                //得到文件名
                String fileName = file.getOriginalFilename();
                //如果文件名不为空
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传保存的最终路径
                    finalCoverPath = fileSpace + uploadPathDB + "/" + fileName;
                    // System.out.println(finalVideoPath+"*****************************************");
                    //设置数据库保存路径
                    uploadPathDB+= ("/" + fileName);
                    File outFile = new File(finalCoverPath);
                    //isDirectory（）如果是目录文件返回true，否则返回false
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    //通过ioutil 对接输入输出流，实现文件下载
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }
            else{
                return IMoocJSONResult.errorMsg("文件名为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        videoService.updataVideo(videoId,uploadPathDB);
        return  IMoocJSONResult.ok();

    }
}
