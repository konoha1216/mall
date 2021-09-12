package com.mct.mall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @description: create orde number utils
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/12 7:19 下午
 * @version:v1.0
 */
public class OrderCodeFactory {

    private static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    private static int getRandom(Long n) {
        Random random = new Random();
        return (int) (random.nextDouble() * 90000) + 10000;
    }

    public static String getOrderCode(Long userId) {
        return getDateTime() + getRandom(userId);
    }
}
