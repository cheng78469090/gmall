package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 19:45
 * @Description:
 */
public interface GmallWmsApi {
    @GetMapping("wms/waresku/{skuId}")
     Resp<List<WareSkuEntity>> queryWareSkuBySkuId(
            @PathVariable("skuId")Long skuId
    );
}
