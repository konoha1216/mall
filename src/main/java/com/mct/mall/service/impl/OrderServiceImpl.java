package com.mct.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mct.mall.common.Constant;
import com.mct.mall.common.Constant.Cart;
import com.mct.mall.common.Constant.OrderStatusEnum;
import com.mct.mall.common.Constant.SaleStatus;
import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.filter.UserFilter;
import com.mct.mall.model.dao.CartMapper;
import com.mct.mall.model.dao.OrderItemMapper;
import com.mct.mall.model.dao.OrderMapper;
import com.mct.mall.model.dao.ProductMapper;
import com.mct.mall.model.pojo.Order;
import com.mct.mall.model.pojo.OrderItem;
import com.mct.mall.model.pojo.Product;
import com.mct.mall.model.request.CreateOrderRequest;
import com.mct.mall.model.vo.CartVO;
import com.mct.mall.model.vo.OrderItemVO;
import com.mct.mall.model.vo.OrderVO;
import com.mct.mall.service.CartService;
import com.mct.mall.service.OrderService;
import com.mct.mall.util.OrderCodeFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @description: order service implement class
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/9/12 6:19 下午
 * @version:v1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    CartService cartService;

    @Resource
    ProductMapper productMapper;

    @Resource
    CartMapper cartMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderRequest request) {
        // get user id
        Integer userId = UserFilter.currentUser.getId();
        // find 'selected' record in the cart
        List<CartVO> cartVOList = cartService.list(userId);
        List<CartVO> selectedList = cartVOList.stream().filter(e -> e.getSelected().equals(Cart.CHECKED)).collect(Collectors.toList());
        // if no 'selected' record, return error
        if (CollectionUtils.isEmpty(selectedList)) {
            throw new MallException(MallExceptionEnum.CART_EMPTY);
        }
        // check product exist, on sale status, stock
        validSaleStatusAndStock(selectedList);
        // cart object to order item object
        List<OrderItem> orderItemList = cartVOListToOrderItemList(selectedList);
        // deduct the stock
        for (OrderItem orderItem: orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // delete 'selected' record in the cart
        cleanCart(selectedList);
        // create order
        Order order = new Order();
        // create order number under the specific rule
        String orderCode = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderCode);
        // set order info
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setReceiverMobile(request.getReceiverMobile());
        order.setOrderStatus(OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // insert into order table
        orderMapper.insertSelective(order);
        // loop save each record in the order_item table
        for (OrderItem orderItem: orderItemList) {
            orderItem.setOrderNo(orderCode);
            orderItemMapper.insertSelective(orderItem);
        }
        // return the result
        return orderCode;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = orderItemList.stream().map(OrderItem::getTotalPrice).reduce((a,b) -> a+b).get();
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO: cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        ArrayList<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO: cartVOList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            // product info
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> selectedList) {
        for (CartVO cartVO: selectedList) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            // check if exist and on sale now
            if (product == null || product.getStatus().equals(SaleStatus.NOT_SALE)) {
                throw new MallException(MallExceptionEnum.NOT_SALE);
            }

            // check the stock
            if (cartVO.getQuantity() > product.getStock()) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderCode) {
        Order order = orderMapper.selectByOrderCode(orderCode);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        // if order exists, we need to check if the user is correct
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new MallException(MallExceptionEnum.NOT_YOUR_ORDER);
        }

        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        // need to operate orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderCode(order.getOrderNo());
        ArrayList<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem: orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);

        orderVO.setOrderStatusName(OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        ArrayList<OrderVO> orderVOList = new ArrayList<>();
        for (Order order: orderList) {
            orderVOList.add(getOrderVO(order));
        }
        return orderVOList;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderCode(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }

        // validate the user
        // if order exists, we need to check if the user is correct
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new MallException(MallExceptionEnum.NOT_YOUR_ORDER);
        }

        if (order.getOrderStatus().equals(OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

}
