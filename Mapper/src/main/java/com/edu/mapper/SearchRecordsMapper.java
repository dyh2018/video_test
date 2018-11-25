package com.edu.mapper;

import com.imooc.pojo.SearchRecords;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    //得到热搜词列表
    public List<String>getHotWords();
}