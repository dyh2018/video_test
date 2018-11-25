package com.edu.controller;


import com.edu.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bgm")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @PostMapping("/list")
    public IMoocJSONResult queryBgm(){
        return IMoocJSONResult.ok(bgmService.queryBgmInfo());
    }
}
