package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 18:12
 * @Description:
 */
public interface IndexService {
    List<CategoryEntity> queryLvl1Categories();

    List<CategoryVo> queryCategoriesWithSub(Long pid);
}
