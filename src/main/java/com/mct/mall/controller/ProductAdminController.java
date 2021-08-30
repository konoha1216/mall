package com.mct.mall.controller;

import com.github.pagehelper.PageInfo;
import com.mct.mall.common.ApiRestResponse;
import com.mct.mall.common.Constant;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.AddProductRequest;
import com.mct.mall.model.request.UpdateProductRequest;
import com.mct.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 11:39 下午
 * @version:v1.0
 */
@Controller
public class ProductAdminController {
    @Resource
    ProductService productService;

    @ApiOperation("添加商品")
    @PostMapping("admin/product/add")
    @ResponseBody
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductRequest addProductRequest) {
        productService.addProduct(addProductRequest);
        return ApiRestResponse.success();
    }

    @ApiOperation("上传图片")
    @PostMapping("admin/upload/file")
    @ResponseBody
    public ApiRestResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // 文件后缀.JPG .PNG等保留
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffixName;
        // 创建文件
        File fileDir = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);

        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(request.getRequestURL().toString())) + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MallExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("更新商品")
    @PostMapping("admin/product/update")
    @ResponseBody
    public ApiRestResponse update(@Valid @RequestBody UpdateProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        productService.updateProduct(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("删除商品")
    @PostMapping("admin/product/delete")
    @ResponseBody
    public ApiRestResponse delete(@Valid @RequestParam Integer id) {
        productService.deleteProduct(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("批量上下架商品")
    @PostMapping("admin/product/batchUpdateSellStatus")
    @ResponseBody
    public ApiRestResponse batchUpdateSellStatus(@Valid @RequestParam Integer[] ids, @Valid @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @PostMapping("admin/product/list")
    @ResponseBody
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}
