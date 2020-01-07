package com.atguigu.gmall.pms.service.impl;


import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.pms.Vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.Vo.SkuInfoVO;
import com.atguigu.gmall.pms.Vo.SpuInfoVO;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long catId) {
        //封装分页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);
        //封装查询条件
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        //判断是否为零
        if (catId!=0){
             queryWrapper.eq("catalog_id",catId);
        }
        //根据用户输入的查询条件搜索
        String key=condition.getKey();
        if (StringUtils.isNotBlank(key)){

            queryWrapper.and(t -> t.like("spu_name", key).or().like("id", key));
        }
        return new PageVo(this.page(page,queryWrapper));
    }

    /**
     * 保存信息方法
     * @param spuInfoVO
     */
    @GlobalTransactional
    @Override
    public void saveSuInfoVo(SpuInfoVO spuInfoVO) {
        this.saveSpuInfo(spuInfoVO);

        this.spuInfoDescService.saveSpuDesc(spuInfoVO);

        this.saveBaseAttrs(spuInfoVO);

        this.saveSkuInfoWithSaleInfo(spuInfoVO);


    }
    @Transactional
    public void saveSkuInfoWithSaleInfo(SpuInfoVO spuInfoVO) {
        /// 2. 保存sku相关信息
        List<SkuInfoVO> skuInfoVOs = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skuInfoVOs)){
            return;
        }
        skuInfoVOs.forEach(skuInfoVO -> {
            // 2.1. 保存sku基本信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO, skuInfoEntity);
            // 品牌和分类的id需要从spuInfo中获取
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            // 获取随机的uuid作为sku的编码
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0, 10).toUpperCase());
            // 获取图片列表
            List<String> images = skuInfoVO.getImages();
            // 如果图片列表不为null，则设置默认图片
            if (!CollectionUtils.isEmpty(images)){
                // 设置第一张图片作为默认图片
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null ? images.get(0) : skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoEntity.setSpuId(spuInfoVO.getId());
            this.skuInfoDao.insert(skuInfoEntity);
            // 获取skuId
            Long skuId = skuInfoEntity.getSkuId();

            // 2.2. 保存sku图片信息
            if (!CollectionUtils.isEmpty(images)){
                String defaultImage = images.get(0);
                List<SkuImagesEntity> skuImageses = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultImg(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgSort(0);
                    skuImagesEntity.setImgUrl(image);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImageses);
            }

            // 2.3. 保存sku的规格参数（销售属性）
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            saleAttrs.forEach(saleAttr -> {
                // 设置属性名，需要根据id查询AttrEntity
                //saleAttr.setAttrName(this.attrDao.selectById(saleAttr.getId()).getAttrName());
                saleAttr.setAttrSort(0);
                saleAttr.setSkuId(skuId);
            });
            this.skuSaleAttrValueService.saveBatch(saleAttrs);





            // 3. 保存营销相关信息，需要远程调用gmall-sms
            // 3.1. 积分优惠
            SkuSaleVo saleVO = new SkuSaleVo();
            BeanUtils.copyProperties(skuInfoVO, saleVO);
            saleVO.setSkuId(skuId);
            this.smsClient.saveSales(saleVO);
        });
    }
    @Transactional
    public void saveBaseAttrs(SpuInfoVO spuInfoVO) {
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> productAttrValueEntitie= baseAttrs.stream().map(
                    productAttrValueVO -> {
                        productAttrValueVO.setSpuId(spuInfoVO.getId());
                        productAttrValueVO.setAttrSort(0);
                        productAttrValueVO.setQuickShow(0);
                        return productAttrValueVO;
                    }
            ).collect(Collectors.toList());
            this.productAttrValueService.saveBatch(productAttrValueEntitie);
        }
    }
    //保存图片信息
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSpuDesc(SpuInfoVO spuInfoVO) {
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoVO.getId());
        spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(),","));
        this.spuInfoDescDao.insert(spuInfoDescEntity);
    }
    @Transactional
    public void saveSpuInfo(SpuInfoVO spuInfoVO) {
        //1.保存spuinfo
        spuInfoVO.setPublishStatus(1);//默认是已经上架了的
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
    }

}