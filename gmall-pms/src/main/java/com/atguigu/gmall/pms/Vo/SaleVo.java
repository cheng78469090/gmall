package com.atguigu.gmall.pms.Vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/5 18:30
 * @Description:
 */
@Data
public class SaleVo {
    // 积分活动
    private Long skuId;
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
}
