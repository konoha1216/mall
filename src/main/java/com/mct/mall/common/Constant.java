package com.mct.mall.common;

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

    public static String FILE_UPLOAD_DIR;
    @Value ("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }
}
