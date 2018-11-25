package com.edu.controller;

import com.edu.service.UserService;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.pojo.vo.VideoPublishInfo;
import com.imooc.utils.IMoocJSONResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//@RestController注解相当于@ResponseBody ＋ @Controller
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //更新头像
    @PostMapping("/uploadFace")
    //userId不用加注解的法，默认使用形参名字了
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[]files) throws IOException {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace = "D:/idea_java_project/UserData";
        //保存到数据库里的相对路径
        String uploadPathDB = "/" + userId + "/face";
        //写入文件
        FileOutputStream fileOutputStream = null;
        //读取文件
        InputStream inputStream = null;
        //如果文件存在且长度大于0
        try {
            if (files != null && files.length > 0) {
                //得到文件名
                String fileName = files[0].getOriginalFilename();
                //如果文件名不为空
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传保存的最终路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    System.out.println(finalFacePath+"*****************************************");
                    //设置数据库保存路径
                    uploadPathDB+= ("/" + fileName);
                    File outFile = new File(finalFacePath);
                    //isDirectory（）如果是目录文件返回true，否则返回false
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
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
                if(fileOutputStream!=null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
        }
        Users user=new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInformation(user);
        return IMoocJSONResult.ok(uploadPathDB);
    }
    //查询用户信息
    @PostMapping("/query")
    public  IMoocJSONResult queryInfo(String userId,String fansId){
           // System.out.println(userId+"------userfans------"+fansId);
            if(StringUtils.isBlank(userId)){
                return  IMoocJSONResult.errorMsg("id为空");
            }
            else{
                Users user=userService.queryUserInformation(userId);
                UsersVO usersVO=new UsersVO();
                BeanUtils.copyProperties(user,usersVO);
                //判断是否是粉丝关系
                boolean isFans=userService.isFans(userId,fansId);
                usersVO.setIsFans(isFans);
                return  IMoocJSONResult.ok(usersVO);
            }
    }
    //查询发布视频者的信息
    @PostMapping(value = "/queryPublisherInfo")
    public IMoocJSONResult queryPublisherInfo(String loginUserId,String videoId,String publishId){
        if(StringUtils.isBlank(publishId)){
            return IMoocJSONResult.errorMsg("未查找到发布者Id");
        }
        else{
            //发布者的用户信息
            Users publish=userService.queryUserInformation(publishId);
            UsersVO publisher=new UsersVO();
            BeanUtils.copyProperties(publish,publisher);
            //登录者是否点赞发布者
            Boolean ifUsersLikeVideos=userService.ifUsersLikeVideos(loginUserId, videoId);
            //打包二者信息
            VideoPublishInfo videoPublishInfo=new VideoPublishInfo();
            videoPublishInfo.setPublisher(publisher);
            videoPublishInfo.setUsersLikeVideos(ifUsersLikeVideos);

            return  IMoocJSONResult.ok(videoPublishInfo);
        }
    }
    //用户关注另一个用户
    @PostMapping(value = "/beyourfans")
    public IMoocJSONResult beYourFans(String userId,String fansId){
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(fansId)){
            return  IMoocJSONResult.errorMsg("...");
        }
        else{
            userService.saveUserAndFans(userId,fansId);
            return IMoocJSONResult.ok("关注成功");
        }
    }
    //用户取消关注另一个用户
    @PostMapping(value = "/notbeyourfans")
    public IMoocJSONResult notBeYourFans(String userId,String fansId){
        //System.out.println(userId+"---------"+fansId);
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(fansId)){
            return  IMoocJSONResult.errorMsg("...");
        }
        else{
            userService.deleteUserAndFans(userId,fansId);
            return IMoocJSONResult.ok("取消关注成功");
        }
    }
    //举报视频
    @PostMapping(value = "/reportuser")
    public IMoocJSONResult reportVideos(@RequestBody UsersReport usersReport){
        String userId=usersReport.getUserid();
        if(StringUtils.isBlank(userId)||StringUtils.isEmpty(userId)){
            return IMoocJSONResult.errorMsg("登录啊");
        }
        userService.reportVideos(usersReport);
        return  IMoocJSONResult.ok("举报成功");
    }
    //保存用户对视频的评论
    @PostMapping(value = "/savecomments")
    public IMoocJSONResult saveComments(@RequestBody Comments comments,String fatherCommentId,String toUserId){
        comments.setFatherCommentId(fatherCommentId);
        comments.setToUserId(toUserId);
        userService.saveComment(comments);
        return IMoocJSONResult.ok("保存成功");
    }



}
