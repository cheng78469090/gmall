package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 18:49
 * @Description:
 */
@Data
public class CategoryVo extends CategoryEntity {
    private List<CategoryEntity> subs;
}
