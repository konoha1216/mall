package com.mct.mall.model.dao;

import com.mct.mall.model.pojo.Order;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderCode(@Param("orderCode") String orderCode);

    List<Order> selectForCustomer(@Param("userId") Integer userId);

    List<Order> selectAllForAdmin();
}
