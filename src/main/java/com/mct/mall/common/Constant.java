package com.mct.mall.common;

import com.google.common.collect.Sets;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/10 10:31 下午
 * @version:v1.0
 */
@Component
public class Constant {
    public static final String MCT_MALL_USER = "mct_mall_user";
    public static final String SALT = "fhiuswy4982r2$%f@#$";

    // 处理上传图片时静态资源映射
    public static String FILE_UPLOAD_DIR;
    @Value ("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price asc", "price desc");
    }
}
