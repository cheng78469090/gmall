package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.Vo.AttrGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;




/**
 * 属性分组
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-12-31 16:30:50
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    /**
     * 功能描述: 当添加商品的时候，根据用户选择的商品的分类，显示不同的商品参数，使用户添加相应的值
     * 具体需求：根据分类id查询分组以及组下的属性，生成一个动态表单，提供用户选择
     * @date: 2020/1/4 16:06
     */
    @ApiOperation("根据三级分类id查询分组及组下的规格参数")
    @GetMapping("/withattrs/cat/{catId}")
    public Resp<List<AttrGroupVO>> queryByCid(
            //根据分类id查询所有分组以及组下的属性
            @PathVariable("catId")Long catId){
        List<AttrGroupVO> attrGroupVOS=this.attrGroupService.queryByCid(catId);
        return Resp.ok(attrGroupVOS);
    }

    @ApiOperation("根据分组id查询分组及组下的规格参数")
    @GetMapping("withattr/{gid}")
    public Resp<AttrGroupVO> queryById(@PathVariable("gid")Long gid){
       AttrGroupVO attrGroupVO = this.attrGroupService.queryById(gid);
        return Resp.ok(attrGroupVO);
    }


    @ApiOperation("根据三级分类id分页查询")
    @GetMapping(value = "{cid}")
    public Resp<PageVo> queryByCidPage(
            @PathVariable("cid")Long cid,
            QueryCondition condition
    ){
        /**
         *
         * 功能描述: 根据三级分类id分页查询
         *
         * @param: [cid, condition]
         * @return: com.atguigu.core.bean.Resp<com.atguigu.core.bean.PageVo>
         * @auther: 宋金城
         * @date: 2020/1/3 16:41
         */
        PageVo pageVo=this.attrGroupService.queryByCidPage(cid,condition);
        return Resp.ok(pageVo);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attrgroup:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrGroupService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrGroupId}")
    @PreAuthorize("hasAuthority('pms:attrgroup:info')")
    public Resp<AttrGroupEntity> info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return Resp.ok(attrGroup);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attrgroup:save')")
    public Resp<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attrgroup:update')")
    public Resp<Object> update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attrgroup:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return Resp.ok(null);
    }

}
