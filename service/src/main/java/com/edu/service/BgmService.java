package com.edu.service;

import com.imooc.pojo.Bgm;

import java.util.List;

public interface BgmService {

    //查找bgm列表
    public List<Bgm> queryBgmInfo();
    //根据bgmId查找bgm
    public Bgm queryBgmBybgmId(String bgmId);
}
