package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.Vo.AttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-12-31 16:30:50
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryByCidTypePage(QueryCondition condition, Long cid, Integer type);

    void saveAttrVO(AttrVO attrVO);
}

