package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 18:06
 * @Description:
 */

public interface GmallPmsApi {

    @PostMapping("pms/spuinfo/page")
     Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition condition);

    @GetMapping("pms/skuinfo/{spuId}")
    Resp<List<SkuInfoEntity>> querySkuBySpuId(
            @PathVariable("spuId")Long spuId
    );

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandNameById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryNameById(@PathVariable("catId") Long catId);


    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrValue(
            @PathVariable("spuId")Long spuId
    );

    @GetMapping("pms/skuinfo/info/{id}")
    public Resp<SpuInfoEntity> QuerySpuByIdCreateTime(@PathVariable("id") Long id);

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategoryByLeve(
            @RequestParam(value = "level",defaultValue = "0")Integer level,
            @RequestParam(value = "parentCid",required = false)Long parentCid
    );
    @GetMapping("pms/category/{pid}")
    public Resp<List<CategoryVo>> queryCategoriesWithSub(@PathVariable("pid")Long pid);
}
