package com.edu.service.iml;

import com.edu.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import com.edu.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//没加找不到
@Service
public class BgmServiceIml implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;
    @Override
    public List<Bgm> queryBgmInfo() {
        return bgmMapper.selectAll();
    }

    @Override
    public Bgm queryBgmBybgmId(String bgmId) {

        return  bgmMapper.selectByPrimaryKey(bgmId);
    }
}
