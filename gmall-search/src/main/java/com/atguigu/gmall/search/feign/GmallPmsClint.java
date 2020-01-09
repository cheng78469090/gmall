package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 18:10
 * @Description:
 */
@FeignClient("pms-service")
public interface GmallPmsClint extends GmallPmsApi {
}
