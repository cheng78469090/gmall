package com.atguigu.gmall.search;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClint;
import com.atguigu.gmall.search.feign.GmallWmsClint;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValue;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.repository.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private GmallPmsClint pmsClint;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private GmallWmsClint wmsClint;



    @Test
    void contextLoads() {
        this.restTemplate.createIndex(Goods.class);
        this.restTemplate.putMapping(Goods.class);
    }


    @Test
    void iportData() {
        //分页查询spu信息
        //我们搜索的大多数都是spu信息，通过spu信息获取sku信息
        Long pageSize=100L;
        Long pageNumber=1L;
        //每次查询一页数据，一页中有100条数据，查询到最后一页的时候，总数据肯定不是100，这个时候推出循环
        do{
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(pageNumber);
            queryCondition.setLimit(pageSize);//设置每页有多少条，每次查100条
            Resp<List<SpuInfoEntity>> listResp = this.pmsClint.querySpuPage(queryCondition);
            List<SpuInfoEntity> spuInfoEntities = listResp.getData();//返回对象不肯能为空对象，因为在resp类中已经写new过了对象
            //当前页没有数据直接停止
            if (CollectionUtils.isEmpty(spuInfoEntities)){
                return;
            }
            //遍历数据，遍历spu，查询出所有sku
            spuInfoEntities.forEach(spuInfoEntity -> {
                //根据spuid查询出所有的sku信息
                Resp<List<SkuInfoEntity>> skuResp = this.pmsClint.querySkuBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                //进行判断，判断sku信息是否为空,如果不为空进行添加
                if (!CollectionUtils.isEmpty(skuInfoEntities)){
                    List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                        Goods goods = new Goods();
                        goods.setSkuId(skuInfoEntity.getSkuId());
                        goods.setSale(100L);
                        goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                        goods.setCreatTime(spuInfoEntity.getCreateTime());
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

                        Resp<List<ProductAttrValueEntity>> attrValueResp = this.pmsClint.querySearchAttrValue(spuInfoEntity.getId());
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
            });
            pageSize=(long)spuInfoEntities.size();//这个size是集合里的数据，查询出的每页全部数据，放入到集合当中
            pageNumber++;
            System.out.println("每页一共有："+spuInfoEntities.size());
        }while (pageSize==100);

    }

}
