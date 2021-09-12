package com.mct.mall.service;

import com.mct.mall.model.vo.CartVO;
import java.util.List;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/9 8:09 上午
 * @version:v1.0
 */
public interface CartService {

    List<CartVO> list(Integer userId);

    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> select(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAll(Integer userId, Integer selected);
}
