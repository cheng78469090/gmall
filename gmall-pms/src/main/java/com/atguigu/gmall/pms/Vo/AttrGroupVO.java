package com.atguigu.gmall.pms.Vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/3 17:08
 * @Description:
 */
@Data
public class AttrGroupVO extends AttrGroupEntity {
    private List<AttrEntity> attrEntities;

    private List<AttrAttrgroupRelationEntity> relations;
}
