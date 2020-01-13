package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.CategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;




/**
 * 商品三级分类
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-12-31 16:30:49
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;



    @GetMapping("{pid}")
    public Resp<List<CategoryVo>> queryCategoriesWithSub(@PathVariable("pid")Long pid){
        List<CategoryVo> categoryVoListthis=categoryService.queryCategoriesWithSub(pid);
        return Resp.ok(categoryVoListthis);
    }

    @GetMapping
    public Resp<List<CategoryEntity>> queryCategory(
            @RequestParam(value = "level",defaultValue = "0")Integer level,
            @RequestParam(value = "parentCid",required = false)Long parentCid
    ){
        /**
         *
         * 功能描述: 商品分类功能
         *
         * @param: [level, parentCid]
         * @return: com.atguigu.core.bean.Resp<java.util.List<com.atguigu.gmall.pms.entity.CategoryEntity>>
         * @auther: 宋金城
         * @date: 2020/1/3 15:04
         */
        //分类查询方法
        List<CategoryEntity> categoryEntityList=this.categoryService.queryCategory(level, parentCid);
        System.out.println("查询分页controller执行成功");
        return Resp.ok(categoryEntityList);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:category:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = categoryService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{catId}")
    @PreAuthorize("hasAuthority('pms:category:info')")
    public Resp<CategoryEntity> info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return Resp.ok(category);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:category:save')")
    public Resp<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:category:update')")
    public Resp<Object> update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:category:delete')")
    public Resp<Object> delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return Resp.ok(null);
    }

}
