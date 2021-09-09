package com.mct.mall.service.impl;

import com.mct.mall.common.Constant;
import com.mct.mall.common.Constant.SaleStatus;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.dao.CartMapper;
import com.mct.mall.model.dao.ProductMapper;
import com.mct.mall.model.pojo.Cart;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.vo.CartVO;
import com.mct.mall.service.CartService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/9 8:09 上午
 * @version:v1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Resource ProductMapper productMapper;

    @Resource CartMapper cartMapper;

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        //TODO
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // not in cart before, need to add a new cart record
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            // already in cart, need to update the quantity
            Integer quantity = cart.getQuantity();
            cart.setQuantity(quantity + count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return null;
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        // check if exist and on sale now
        if (product == null || product.getStatus().equals(SaleStatus.NOT_SALE)) {
            throw new MallException(MallExceptionEnum.NOT_SALE);
        }

        // check the stock
        if (count > product.getStock()) {
            throw new MallException(MallExceptionEnum.NOT_ENOUGH);
        }
    }
}
