package com.mct.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.common.Constant;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.pojo.Category;
import com.mct.mall.model.pojo.User;
import com.mct.mall.model.request.AddCategoryRequest;
import com.mct.mall.model.request.UpdateCategoryRequest;
import com.mct.mall.model.vo.CategoryVO;
import com.mct.mall.service.CategoryService;
import com.mct.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 4:02 下午
 * @version:v1.0
 */
@Controller
public class CategoryController {
    @Resource
    UserService userService;
    @Resource
    CategoryService categoryService;

    @ApiOperation("后台添加商品分类")
    @PostMapping("admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryRequest request) {

        User currentUser = (User) session.getAttribute(Constant.MCT_MALL_USER);
        if (currentUser == null ){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        // 管理员才能加
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            categoryService.add(request);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NOT_ADMIN);
        }
    }
    @ApiOperation("后台更新商品分类")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryRequest request, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constant.MCT_MALL_USER);
        if (currentUser == null ){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        // 管理员才能加
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            Category category = new Category();
            BeanUtils.copyProperties(request,category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NOT_ADMIN);
        }
    }

    @ApiOperation("后台删除商品分类")
    @PostMapping("admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @GetMapping("admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @GetMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryService.listForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }
}
