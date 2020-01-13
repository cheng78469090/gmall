package com.atguigu.gmall.index.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 18:13
 * @Description:
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient pmsClient;


    @Override
    public List<CategoryEntity> queryLvl1Categories() {
        Resp<List<CategoryEntity>> listResp = this.pmsClient.queryCategoryByLeve(1, null);
        List<CategoryEntity> categoryEntities = listResp.getData();
        return categoryEntities;
    }

    @Override
    public List<CategoryVo> queryCategoriesWithSub(Long pid) {
        Resp<List<CategoryVo>> listResp = this.pmsClient.queryCategoriesWithSub(pid);
        List<CategoryVo> data = listResp.getData();

        return data;
    }
}
