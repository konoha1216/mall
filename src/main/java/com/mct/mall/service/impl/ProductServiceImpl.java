package com.mct.mall.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mct.mall.common.Constant.ProductListOrderBy;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.dao.ProductMapper;
import com.mct.mall.model.pojo.Category;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.query.ProductListQuery;
import com.mct.mall.model.request.AddProductRequest;
import com.mct.mall.model.request.ProductListRequest;
import com.mct.mall.model.vo.CategoryVO;
import com.mct.mall.service.CategoryService;
import com.mct.mall.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Resource
    CategoryService categoryService;
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

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            throw new MallException(MallExceptionEnum.PRODUCT_FETCH_FAILED);
        }
        return product;
    }

    @Override
    public PageInfo list(ProductListRequest request) {
        ProductListQuery productListQuery = new ProductListQuery();

        if (!StringUtils.isEmpty(request.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(request.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        if (request.getCategoryId() != null) {
            List<CategoryVO> categoryVOList = categoryService.listForCustomer(request.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(request.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        String orderBy = request.getOrderBy();
        if (ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(request.getPageNum(), request.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(request.getPageNum(), request.getPageSize());
        }

        List<Product> productList = productMapper.selectListForUsers(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (int i=0; i<categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

}
