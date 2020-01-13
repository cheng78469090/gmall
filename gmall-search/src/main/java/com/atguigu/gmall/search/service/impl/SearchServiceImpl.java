package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/10 18:40
 * @Description:
 */
@Service
public class SearchServiceImpl implements SearchService {
    //使用哪个客户端完成搜索功能

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public SearchResponseVo searchData(SearchParam searchParam) throws IOException {

        SearchResponse searchResponse = this.restHighLevelClient.search(new SearchRequest(new String[]{"goods"}, buildeDSL(searchParam)), RequestOptions.DEFAULT);
        SearchResponseVo searchResponseVo = this.parsemSearchResponseVo(searchResponse);
        // System.out.println(search);
        //解析数据，解析分页
        searchResponseVo.setPageNum(searchParam.getPageNum());
        searchResponseVo.setPageSize(searchParam.getPageSize());
        return searchResponseVo;
    }
    //解析数据方法
    private SearchResponseVo parsemSearchResponseVo(SearchResponse searchResponse){
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();//查询出的所有结果集
        //遍历取出数据，获取搜索出来的全部商品信息produceds,解析商品结果集
        List<Goods> goodsList=new ArrayList<>();
        for (SearchHit hitsHit : hitsHits) {
            String goodsJson = hitsHit.getSourceAsString();
            //获取_source反序列化为goods
            Goods goods = JSON.parseObject(goodsJson, Goods.class);
            //获取高亮结果集
            Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
            //获取高亮结果集对象
            HighlightField highlightField = highlightFields.get("skuTitle");
            //将高亮对象替换原有属性
            goods.setSkuTitle(highlightField.getFragments()[0].string());
            goodsList.add(goods);
        }
        searchResponseVo.setProducts(goodsList);
        //解析品牌聚合结果集
        Map<String, Aggregation> aggregationsAsMap = searchResponse.getAggregations().asMap();
        ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggregationsAsMap.get("brandId");//根据品牌id聚合
        SearchResponseAttrVO brandIdAttrVO = new SearchResponseAttrVO();
        brandIdAttrVO.setName("品牌");
        brandIdAttrVO.setProductAttributeId(null);
        //每一个品牌里的值也是一个集合
        List<? extends Terms.Bucket> buckets = brandIdAgg.getBuckets();//在这个桶当中有所需要的数据，还包含子桶
        if (!CollectionUtils.isEmpty(buckets)){
            List<String> brandValue = buckets.stream().map(bucket -> {
                //将每个桶转化为json字符串
              //  name就是“品牌” value: [{id:100,name:华为,logo:xxx},{id:101,name:小米,log:yyy}]
                Map<String,Object> brandVlues=new HashMap<>();
                brandVlues.put("id",((Terms.Bucket) bucket).getKeyAsNumber());
                //品牌name的值，在子聚合当中
                Aggregation brand_nameAgg = ((Terms.Bucket) bucket).getAggregations().get("brand_name");
                ParsedStringTerms brandNameBuck=(ParsedStringTerms)brand_nameAgg;
                if (brandNameBuck!=null){
                    brandVlues.put("name",brandNameBuck.getBuckets().get(0).getKeyAsString());
                }
                return JSON.toJSONString(brandVlues);
            }).collect(Collectors.toList());
            brandIdAttrVO.setValue(brandValue);//存放属性值的集合
            searchResponseVo.setBrand(brandIdAttrVO);
        }
        //解析分类聚合结果集
        SearchResponseAttrVO catelogAttrVO=new SearchResponseAttrVO();
        ParsedLongTerms category_idAgg =(ParsedLongTerms)aggregationsAsMap.get("category_id");
        catelogAttrVO.setProductAttributeId(null);
        catelogAttrVO.setName("分类");
        List<? extends Terms.Bucket> category_idAggBuckets = category_idAgg.getBuckets();
        if (!CollectionUtils.isEmpty(category_idAggBuckets)){
            List<String> categoryValue = category_idAggBuckets.stream().map(bucket -> {
                Map<String,Object> categoryMap=new HashMap<>();
                categoryMap.put("id",((Terms.Bucket) bucket).getKeyAsNumber());
                ParsedStringTerms category_nameAggr =(ParsedStringTerms)((Terms.Bucket) bucket).getAggregations().get("category_name");
                categoryMap.put("name",category_nameAggr.getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(categoryMap);
            }).collect(Collectors.toList());
            catelogAttrVO.setValue(categoryValue);
            searchResponseVo.setCatelog(catelogAttrVO);
        }
        //解析规格参数聚合结果集

        ParsedNested attr_Agg=(ParsedNested) aggregationsAsMap.get("attr_agg");

        ParsedLongTerms aggregations = attr_Agg.getAggregations().get("attr_id");
        List<? extends Terms.Bucket> idBuckets = aggregations.getBuckets();

        List<SearchResponseAttrVO> attrVOS = idBuckets.stream().map(bucket -> {
            SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
            searchResponseAttrVO.setProductAttributeId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
            ParsedStringTerms attr_nameAgg = ((Terms.Bucket) bucket).getAggregations().get("attr_name");
            searchResponseAttrVO.setName(attr_nameAgg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms attr_valueAgg = ((Terms.Bucket) bucket).getAggregations().get("attr_value");

            List<String> collect = attr_valueAgg.getBuckets().stream().map(bucketValue -> {
                return ((Terms.Bucket) bucketValue).getKeyAsString();
            }).collect(Collectors.toList());
            searchResponseAttrVO.setValue(collect);
            return searchResponseAttrVO;
        }).collect(Collectors.toList());

        searchResponseVo.setAttrs(attrVOS);



        //总记录数
        searchResponseVo.setTotal(hits.getTotalHits());
        return searchResponseVo;
    }

    private SearchSourceBuilder buildeDSL(SearchParam searchParam){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.构建查询
        //1.1匹配查询 布尔过滤查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.2过滤查询,判断用户是否输入搜索关键字
        if (StringUtils.isEmpty(searchParam.getKey())){
              return searchSourceBuilder;
        }
        boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKey()).operator(Operator.AND));
        //1.2.1品牌过滤
        Long[] brandIdArray = searchParam.getBrand();
        if (brandIdArray!=null&&brandIdArray.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandId",brandIdArray));
        }
        //分类过滤
        Long[] catelog3 = searchParam.getCatelog3();
        if (catelog3!=null&&catelog3.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId",catelog3));
        }
        //1.2.2价格范围过滤
        //构建一个范围对象
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");//参数是根据那个字段来进行排序
        Double priceFrom = searchParam.getPriceFrom();
        if (priceFrom!=null){
              rangeQueryBuilder.gte(priceFrom);
        }
        Double priceTo = searchParam.getPriceTo();
        if (priceTo!=null){
            rangeQueryBuilder.lt(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        //1.2.3 规格属性的过滤
        List<String> props = searchParam.getProps();
        if (!CollectionUtils.isEmpty(props)){
            props.forEach(prop->{
                String[] attrIdAndValue = prop.split(":");
                if (attrIdAndValue!=null&&attrIdAndValue.length==2){
                    String attrId = attrIdAndValue[0];
                    String[] attrValue = attrIdAndValue[1].split("-");
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                    boolQuery.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs",boolQuery, ScoreMode.None));//最后是得分
                }
            });
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //2.排序
        String order = searchParam.getOrder();
        if (StringUtils.isNotBlank(order)){
            String[] orders = order.split(":");
            if (orders!=null&&orders.length==2){
                String orderFiled = orders[0];
                String orderBy = orders[1];
                if (orderFiled.equals(0)){
                    orderFiled="_score";
                }else if(orderFiled.equals(1)){
                    orderFiled="sale";
                }else if (orderFiled.equals(2)){
                    orderFiled="price";
                }else {
                    orderFiled="_score";
                }
                searchSourceBuilder.sort(orderFiled,StringUtils.equals(orderBy,"asc")? SortOrder.ASC:SortOrder.DESC);
            }
        }
        //4.分页
        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        searchSourceBuilder.from((pageNum-1)*pageSize);
        //采用默认的
        searchSourceBuilder.size(pageSize);

        //3.高亮
        searchSourceBuilder.highlighter(new HighlightBuilder().field("skuTitle").preTags("<em>").postTags("</em>"));
        //5.聚合
        //品牌聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("brandId")
           .field("brandId").subAggregation(AggregationBuilders.terms("brand_name").field("brandName"))
        );
        //分类聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("category_id").field("categoryId")
        .subAggregation(AggregationBuilders.terms("category_name").field("categoryName")));
        System.out.println(searchSourceBuilder);
        //属性聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("attr_agg", "attrs").subAggregation(
                        AggregationBuilders.terms("attr_id").field("attrs.attrId").subAggregation(
                                AggregationBuilders.terms("attr_name").field("attrs.attrName")
                        ).subAggregation(
                                AggregationBuilders.terms("attr_value").field("attrs.attrValue")
                        )
                )
        );
        return searchSourceBuilder;
    }

}
