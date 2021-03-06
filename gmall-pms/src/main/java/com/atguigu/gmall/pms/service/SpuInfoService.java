package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.Vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-12-31 16:30:50
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuInfo(QueryCondition condition, Long catId);

    void saveSuInfoVo(SpuInfoVO spuInfoVO);
    void saveSkuInfoWithSaleInfo(SpuInfoVO spuInfoVO);
    void saveBaseAttrs(SpuInfoVO spuInfoVO);
    void saveSpuDesc(SpuInfoVO spuInfoVO);
    void saveSpuInfo(SpuInfoVO spuInfoVO);
}

