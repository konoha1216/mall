package com.mct.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.dao.CategoryMapper;
import com.mct.mall.model.pojo.Category;
import com.mct.mall.model.request.AddCategoryRequest;
import com.mct.mall.model.vo.CategoryVO;
import com.mct.mall.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 4:20 下午
 * @version:v1.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    CategoryMapper categoryMapper;
    @Override
    public void add(AddCategoryRequest addCategoryRequest) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryRequest, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryRequest.getName());
        if (categoryOld != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int cnt = categoryMapper.insertSelective(category);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.ADD_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new MallException(MallExceptionEnum.NAME_EXISTED);
            }
        }

        int cnt = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
        int cnt = categoryMapper.deleteByPrimaryKey(id);
        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    @Override
    @Cacheable(value = "listForCustomer")
    public List<CategoryVO> listForCustomer() {
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategory(categoryVOList, 0);
        return categoryVOList;
    }

    public void recursivelyFindCategory(List<CategoryVO> categoryVOList, Integer parentId) {
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category: categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                recursivelyFindCategory(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
