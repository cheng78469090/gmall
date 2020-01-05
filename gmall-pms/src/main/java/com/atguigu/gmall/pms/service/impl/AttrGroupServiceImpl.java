package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.Vo.AttrGroupVO;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    /**
     *
     */
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryByCidPage(Long cid, QueryCondition condition) {
        /**
         *
         * 功能描述: 实现根据三级id分页查询
         *
         * @param: [cid, condition]
         * @return: com.atguigu.core.bean.PageVo
         * @auther: 宋金城
         * @date: 2020/1/3 16:41
         */
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid)
        );
        return new PageVo(page);
    }

    @Override
    public AttrGroupVO queryById(Long gid) {
        AttrGroupVO attrGroupVO = new AttrGroupVO();
        //查询分组
        //AttrEntity attrEntity = this.attrDao.selectById(gid);
        AttrGroupEntity attrGroupEntity = this.attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroupEntity,attrGroupVO);
        //查询分组下的关联关系
       /* List<AttrAttrgroupRelationEntity> relations = this.relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));*/
        List<AttrAttrgroupRelationEntity> relations = this.relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid)
        );
        //判断关联管理系是否为空
        if (CollectionUtils.isEmpty(relations)){
            return attrGroupVO;
        }
        attrGroupVO.setRelations(relations);

        //收集分组下的所有规格id
        List<Long> attrIds = relations.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());
        //查询所有分组下的所有规格参数
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(attrIds);
        attrGroupVO.setAttrEntities(attrEntities);
        return attrGroupVO;
    }

    @Override
    public List<AttrGroupVO> queryByCid(Long catId) {
        //查询所有分组
        List<AttrGroupEntity> attrGroupEntitys = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //查询分组下的所有规格参数
        List<AttrGroupVO> attrGroupVOList = attrGroupEntitys.stream().map(attrGroupEntity -> {
            return this.queryById(attrGroupEntity.getAttrGroupId());
        }).collect(Collectors.toList());
        return attrGroupVOList;
    }


}