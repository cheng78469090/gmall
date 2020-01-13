package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.CategoryDao;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> queryCategory(Integer level, Long parentCid) {
        /**
         *
         * 功能描述: 商品分类功能实现
         *
         * @param: [level, parentCid]
         * @return: java.util.List<com.atguigu.gmall.pms.entity.CategoryEntity>
         * @auther: 宋金城
         * @date: 2020/1/3 15:10
         */
        //构造查询条件
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        //如果没有查询等级代表查询所有
        if (level!=0){
            queryWrapper.eq("cat_level",level);
        }
        //如果parentCid为null，说明父节点id为空，查询所有
        if (parentCid!=null){
            queryWrapper.eq("parent_cid",parentCid);
        }

        return this.categoryDao.selectList(queryWrapper);
    }

    @Override
    public List<CategoryVo> queryCategoriesWithSub(Long pid) {


        return this.categoryDao.queryCategoriesWithSub(pid);
    }

}