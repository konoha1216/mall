package com.mct.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.model.request.CreateOrderRequest;
import com.mct.mall.model.vo.OrderVO;
import com.mct.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/12 6:14 下午
 * @version:v1.0
 */
@RestController
public class OrderController {

    @Resource
    OrderService orderService;

    @ApiOperation("create a order")
    @PostMapping("order/create")
    public ApiRestResponse create(@RequestBody @Valid CreateOrderRequest request) {
        String orderCode = orderService.create(request);
        return ApiRestResponse.success(orderCode);
    }

    @ApiOperation("show the detail of a order")
    @GetMapping("order/detail")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @ApiOperation("show the order list at front end")
    @GetMapping("order/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("cancel a order")
    @PostMapping("order/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("create a order QR code")
    @PostMapping("order/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }
}
