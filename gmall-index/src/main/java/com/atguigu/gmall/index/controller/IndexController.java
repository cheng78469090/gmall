package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 18:11
 * @Description:
 */
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;
    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryLvl1Categories(){
        List<CategoryEntity> categoryEntityList=this.indexService.queryLvl1Categories();
        return Resp.ok(categoryEntityList);
    }

    @GetMapping("cates/{pid}")
    public Resp<List<CategoryVo>> queryCategoriesWithSub(@PathVariable("pid")Long pid){
        List<CategoryVo> categoryVoList=this.indexService.queryCategoriesWithSub(pid);
        return Resp.ok(categoryVoList);
    }

}
