package com.mct.mall.service;

import com.github.pagehelper.PageInfo;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.AddProductRequest;
import com.mct.mall.model.request.ProductListRequest;
import org.springframework.web.bind.annotation.RequestParam;

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

    PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListRequest request);
}
