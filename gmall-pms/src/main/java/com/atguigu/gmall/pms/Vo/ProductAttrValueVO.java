package com.atguigu.gmall.pms.Vo;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/4 16:50
 * @Description:
 */
public class ProductAttrValueVO extends ProductAttrValueEntity {
    public void setValueSelected(List<Object> valueSelected){
        // 如果接受的集合为空，则不设置
        if (CollectionUtils.isEmpty(valueSelected)){
            return;
        }
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
