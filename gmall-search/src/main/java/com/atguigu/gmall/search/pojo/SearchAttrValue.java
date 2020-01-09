package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 17:05
 * @Description:规格参数
 */
@Data
public class SearchAttrValue {

    //规格id
    @Field(type = FieldType.Long)
    private Long attrId;
    //规格名称
    @Field(type = FieldType.Keyword)
    private String attrName;
    //规格值
    @Field(type = FieldType.Keyword)
    private String attrValue;
}
