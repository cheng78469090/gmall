package com.atguigu.gmall.search.feign;
import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 19:50
 * @Description:
 */
@FeignClient("wms-service")
public interface GmallWmsClint extends GmallWmsApi {

}
