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

    @Override
    public void updateProduct(Product product) {
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null && !productOld.getId().equals(product.getId())) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int cnt = productMapper.updateByPrimaryKeySelective(product);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void deleteProduct(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }

        int cnt = productMapper.deleteByPrimaryKey(id);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        int cnt = productMapper.batchUpdateSellStatus(ids, sellStatus);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
        if (cnt > 0 && cnt != ids.length) {
            throw new MallException(MallExceptionEnum.UPDATE_PART_FAILED);
        }
    }
}
