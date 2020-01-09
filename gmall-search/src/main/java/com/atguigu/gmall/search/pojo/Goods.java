package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 16:46
 * @Description:
 * 包含需要es所需要保存的字段
 */
@Data
@Document(indexName = "goods",type = "info",shards = 3,replicas = 2)
public class Goods {
    /**查询出的所有记录都是sku信息，
     * 所有要根据sku信息来设计字段
     */
    @Id
    private Long skuId;
    //标题
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String skuTitle;
    //副标题,不需要根据副标题字段建立索引
    @Field(type = FieldType.Keyword,index = false)
    private String skuSubTitle;
    //价格
    @Field(type = FieldType.Double)
    private double price;
    //图片信息
    @Field(type = FieldType.Keyword,index = false)
    private String defaultImg;


    //销量
    @Field(type = FieldType.Long)
    private Long sale;
    //创建时间
    @Field(type = FieldType.Date)
    private Date creatTime;
    //库存
    @Field(type = FieldType.Boolean)
    private boolean store;

    //品牌id
    @Field(type = FieldType.Long)
    private Long brandId;
    //品牌名称
    @Field(type = FieldType.Keyword)
    private String brandName;
    //分类id
    @Field(type = FieldType.Long)
    private Long categoryId;
    //分类名称
    @Field(type = FieldType.Keyword)
    private String categoryName;

    //规格参数不固定，所以使用集合
    @Field(type = FieldType.Nested)
    private List<SearchAttrValue> attrs;





}
