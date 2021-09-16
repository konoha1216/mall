package com.mct.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.ProductListRequest;
import com.mct.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/30 10:47 下午
 * @version:v1.0
 */
@RestController
public class ProductController {
    @Resource
    ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("product/detail")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product detail = productService.detail(id);
        return ApiRestResponse.success(detail);
    }

    @ApiOperation("商品列表")
    @GetMapping("product/list")
    public ApiRestResponse list(ProductListRequest request) {
        PageInfo pageInfo = productService.list(request);
        return ApiRestResponse.success(pageInfo);
    }
}
