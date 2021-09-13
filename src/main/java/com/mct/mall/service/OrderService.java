package com.mct.mall.service;

import com.github.pagehelper.PageInfo;
import com.mct.mall.model.request.CreateOrderRequest;
import com.mct.mall.model.vo.OrderVO;

/**
 * @description: order service
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/12 6:19 下午
 * @version:v1.0
 */
public interface OrderService {


    String create(CreateOrderRequest request);

    OrderVO detail(String orderCode);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);
}
