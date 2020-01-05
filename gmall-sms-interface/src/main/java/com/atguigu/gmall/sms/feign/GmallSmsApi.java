package com.atguigu.gmall.sms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/5 18:58
 * @Description:
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/skusale/save")
    public Resp<Object> saveSales(@RequestBody SkuSaleVo saleVO);
}
