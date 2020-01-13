package com.atguigu.gmall.search.listener;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClint;
import com.atguigu.gmall.search.feign.GmallWmsClint;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValue;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 16:32
 * @Description:
 */
@Component
public class PmsListener {
    @Autowired
    private GmallPmsClint pmsClint;
    @Autowired
    private GmallWmsClint wmsClint;
    @Autowired
    private GoodsRepository repository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-SEARCH-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert"}
    ))
    public  void listener(Long spuId){
        Resp<List<SkuInfoEntity>> skuResp = this.pmsClint.querySkuBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
        //进行判断，判断sku信息是否为空,如果不为空进行添加
        if (!CollectionUtils.isEmpty(skuInfoEntities)){
            List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                Goods goods = new Goods();
                goods.setSkuId(skuInfoEntity.getSkuId());
                goods.setSale(100L);
                goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                Resp<SpuInfoEntity> spuInfoEntityResp = this.pmsClint.QuerySpuByIdCreateTime(spuId);
                goods.setCreatTime(spuInfoEntityResp.getData().getCreateTime());
                goods.setCategoryId(skuInfoEntity.getCatalogId());
                goods.setBrandId(skuInfoEntity.getBrandId());
                goods.setDefaultImg(skuInfoEntity.getSkuDefaultImg());
                goods.setSkuSubTitle(skuInfoEntity.getSkuSubtitle());
                goods.setSkuTitle(skuInfoEntity.getSkuTitle());
                //根据skuid查询库存
                Resp<List<WareSkuEntity>> wareResp = this.wmsClint.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntityList = wareResp.getData();
                if (!CollectionUtils.isEmpty(wareSkuEntityList)){
                    goods.setStore(wareSkuEntityList.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()>0));
                }
                //添加分类名称
                Resp<CategoryEntity> categoryEntityResp = this.pmsClint.queryCategoryNameById(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = categoryEntityResp.getData();
                if (categoryEntity!=null){
                    goods.setCategoryName(categoryEntity.getName());
                }
                //添加品牌名称
                Resp<BrandEntity> brandEntityResp = this.pmsClint.queryBrandNameById(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandEntityResp.getData();
                if (brandEntity!=null){
                    goods.setBrandName(brandEntity.getName());
                }
                Resp<List<ProductAttrValueEntity>> attrValueResp = this.pmsClint.querySearchAttrValue(spuId);
                List<ProductAttrValueEntity> attrValueEntities = attrValueResp.getData();
                List<SearchAttrValue> attrValues = attrValueEntities.stream().map(attrValueEntitie -> {
                    SearchAttrValue searchAttrValue = new SearchAttrValue();
                    searchAttrValue.setAttrId(attrValueEntitie.getAttrId());
                    searchAttrValue.setAttrName(attrValueEntitie.getAttrName());
                    searchAttrValue.setAttrValue(attrValueEntitie.getAttrValue());
                    return searchAttrValue;
                }).collect(Collectors.toList());
                goods.setAttrs(attrValues);
                return goods;
            }).collect(Collectors.toList());
            this.repository.saveAll(goodsList);
        }
    }

}
