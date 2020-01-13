package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVo;

import java.io.IOException;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/10 18:40
 * @Description:
 */
public interface SearchService {
    SearchResponseVo searchData(SearchParam searchParam) throws IOException;
}
