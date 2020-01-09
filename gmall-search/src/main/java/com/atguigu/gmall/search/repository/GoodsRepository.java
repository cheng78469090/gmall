package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 19:21
 * @Description:
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
