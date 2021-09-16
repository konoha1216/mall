package com.mct.mall.controller;

import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.filter.UserFilter;
import com.mct.mall.model.vo.CartVO;
import com.mct.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
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

    @ApiOperation("query cart list")
    @GetMapping("/list")
    public ApiRestResponse list() {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.list(userId);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("add a product record into the cart")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.add(userId, productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("update a record in the cart")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.update(userId, productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("delete a record in the cart")
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.delete(userId, productId);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("change the 'selected' status of a record in the cart")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.select(userId, productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("change all the 'selected' status of records under a user in the cart")
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.selectAll(userId, selected);
        return ApiRestResponse.success(cartVOS);
    }
}
