package com.mct.mall.exception;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/8 10:02 下午
 * @version:v1.0
 */
public enum MallExceptionEnum {
    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度不能小于8位"),
    NAME_EXISTED(10004, "名字不能重复"),
    INSERT_FAILED(10005, "插入失败"),
    WONG_PASSWORD(10006, "密码错误"),
    NEED_LOGIN(10007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NOT_ADMIN(10009, "非管理员身份"),
    PARAM_NOT_NULL(10010, "参数不能为空"),
    ADD_FAILED(10011, "新增失败"),
    REQUEST_PARAM_ERROR(10012, "参数错误"),
    DELETE_FAILED(10013, "删除失败"),
    MKDIR_FAILED(10014, "创造文件夹失败"),
    UPLOAD_FAILED(10015, "图片上传失败"),
    UPDATE_PART_FAILED(10016, "部分更新失败"),

    SYSTEM_ERROR(20001,"系统异常");
    Integer code;
    String msg;

    MallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
