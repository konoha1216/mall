package com.mct.mall.model.query;

import java.util.List;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/5 9:47 下午
 * @version:v1.0
 */
public class ProductListQuery {

    private String keyword;

    private List<Integer> categoryIds;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
