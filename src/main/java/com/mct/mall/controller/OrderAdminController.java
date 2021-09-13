package com.mct.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/13 10:01 下午
 * @version:v1.0
 */
@RestController
public class OrderAdminController {

    @Resource
    OrderService orderService;

    @GetMapping("admin/order/list")
    @ApiOperation("order list for admin")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}
