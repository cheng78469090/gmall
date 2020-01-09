package com.atguigu.gmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 16:39
 * @Description:
 */
@Configuration
public class ElasticsearchConfig {

    //使用elasticsearch高级客户端查询需要此配置，如果使用其他两种可以不用配置
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.25.128",9200,"http")
                )
        );
    }
}
