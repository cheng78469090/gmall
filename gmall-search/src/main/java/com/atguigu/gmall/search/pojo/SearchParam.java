package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.apache.commons.codec.language.bm.Lang;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/10 18:27
 * @Description:
 */
@Data
public class SearchParam {
    private String key;

    private Long[] catelog3;

    private Long[] brand;

    private String order;

    private Integer pageNum=1;

    private List<String> props;

    private Integer pageSize=3;

    private Double priceFrom;

    private Double priceTo;

    private boolean store;
}
