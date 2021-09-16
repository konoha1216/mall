package com.mct.mall.common;

import com.google.common.collect.Sets;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
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

    public interface SaleStatus {
        int NOT_SALE = 0;
        int SALE = 1;
    }

    public interface Cart {
        int UN_CHECKED = 0; // cart record not selected
        int CHECKED = 1; // cart record selected
    }

    public enum OrderStatusEnum {
        CANCELED(0, "已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "已完成");

        private String value;
        private int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum: values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new MallException(MallExceptionEnum.NO_ENUM);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
