package com.atguigu.gmall.pms.Vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/4 16:51
 * @Description:
 */
@Data
public class SkuInfoVO extends SkuInfoEntity {

    private List<String> images;

    // 积分活动
    private BigDecimal growBounds;
    private BigDecimal buyBounds;

    private List<Integer> work;

    // 满减活动
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    private Integer fullCount;
    private BigDecimal discount;
    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer addOther;

    private List<SkuSaleAttrValueEntity> saleAttrs;
}
