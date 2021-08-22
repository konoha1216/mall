package com.mct.mall.controller;

import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.model.request.AddProductRequest;
import com.mct.mall.service.ProductService;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 11:39 下午
 * @version:v1.0
 */
@Controller
public class ProductAdminController {
    @Resource
    ProductService productService;

    @PostMapping("admin/product/add")
    @ResponseBody
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductRequest addProductRequest) {
        productService.addProduct(addProductRequest);
        return ApiRestResponse.success();
    }


}
