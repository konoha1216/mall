package com.mct.mall.exception;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/8 10:28 下午
 * @version:v1.0
 */
public class MallException extends RuntimeException{
    private final Integer code;
    private final String msg;

    public MallException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MallException(MallExceptionEnum e) {
        this(e.getCode(), e.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
