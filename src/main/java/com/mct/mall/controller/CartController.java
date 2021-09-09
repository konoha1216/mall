package com.mct.mall.controller;

import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.filter.UserFilter;
import com.mct.mall.model.vo.CartVO;
import com.mct.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/9 7:57 上午
 * @version:v1.0
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    CartService cartService;

    @ApiOperation("add a product record into the cart")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> add = cartService.add(userId, productId, count);
        return ApiRestResponse.success();
    }

}
