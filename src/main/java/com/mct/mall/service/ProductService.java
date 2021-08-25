package com.mct.mall.service;

import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.AddProductRequest;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 11:49 下午
 * @version:v1.0
 */
public interface ProductService {

    void addProduct(AddProductRequest addProductRequest);

    void updateProduct(Product product);

    void deleteProduct(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);
}
