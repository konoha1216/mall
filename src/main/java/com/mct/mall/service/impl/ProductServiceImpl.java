package com.mct.mall.service.impl;

import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.dao.ProductMapper;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.AddProductRequest;
import com.mct.mall.service.ProductService;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 11:49 下午
 * @version:v1.0
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    ProductMapper productMapper;

    @Override
    public void addProduct(AddProductRequest addProductRequest) {
        Product productOld = productMapper.selectByName(addProductRequest.getName());
        if (productOld != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(addProductRequest, product);
        int cnt = productMapper.insertSelective(product);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.ADD_FAILED);
        }
    }
}
