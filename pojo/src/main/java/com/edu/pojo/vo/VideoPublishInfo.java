package com.edu.pojo.vo;

public class VideoPublishInfo {
    //发布者的用户信息
    private UsersVO publisher;
    //登录者是否喜欢发布者的视频
    private Boolean usersLikeVideos;

    public UsersVO getPublisher() {
        return publisher;
    }

    public void setPublisher(UsersVO usersVO) {
        this.publisher = usersVO;
    }

    public Boolean getUsersLikeVideos() {
        return usersLikeVideos;
    }

    public void setUsersLikeVideos(Boolean usersLikeVideos) {
        this.usersLikeVideos = usersLikeVideos;
    }
}
