package com.mct.mall.service;

import com.github.pagehelper.PageInfo;
import com.mct.mall.model.pojo.Category;
import com.mct.mall.model.request.AddCategoryRequest;
import com.mct.mall.model.vo.CategoryVO;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 4:19 下午
 * @version:v1.0
 */
public interface CategoryService {

    void add(AddCategoryRequest addCategoryRequest);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);

    List<CategoryVO> listForCustomer(Integer parentId);
}
