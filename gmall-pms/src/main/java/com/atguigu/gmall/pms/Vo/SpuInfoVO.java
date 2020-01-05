package com.atguigu.gmall.pms.Vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/4 16:45
 * @Description:
 */
@Data
public class SpuInfoVO extends SpuInfoEntity{
    // 图片信息
    private List<String> spuImages;

    // 基本属性信息
    private List<ProductAttrValueVO> baseAttrs;

    // sku信息
    private List<SkuInfoVO> skus;

}
