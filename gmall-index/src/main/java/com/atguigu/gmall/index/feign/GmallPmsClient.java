package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/12 18:24
 * @Description:
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
