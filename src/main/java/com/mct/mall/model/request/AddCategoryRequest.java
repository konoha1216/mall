package com.mct.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/22 4:07 下午
 * @version:v1.0
 */
public class AddCategoryRequest {
    @Size(min = 2,max = 5)
    @NotNull(message = "name 不能为null")
    private String name;

    @NotNull(message = "type 不能为null")
    @Max(3)
    private Integer type;

    @NotNull(message = "parentId 不能为null")
    private Integer parentId;

    @NotNull(message = "orderNum 不能为null")
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "AddCategoryRequest{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", parentId=" + parentId +
            ", orderNum=" + orderNum +
            '}';
    }
}
