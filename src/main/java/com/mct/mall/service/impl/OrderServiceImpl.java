package com.mct.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
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
import com.mct.mall.model.pojo.User;
import com.mct.mall.model.request.CreateOrderRequest;
import com.mct.mall.model.vo.CartVO;
import com.mct.mall.model.vo.OrderItemVO;
import com.mct.mall.model.vo.OrderVO;
import com.mct.mall.service.CartService;
import com.mct.mall.service.OrderService;
import com.mct.mall.service.UserService;
import com.mct.mall.util.OrderCodeFactory;
import com.mct.mall.util.QRCodeGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @Resource
    UserService userService;

    @Value("${file.upload.ip}")
    String ip;

    final static int width = 350;

    final static int height = 350;

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
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
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

    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 本季机测试用
//        try {
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generateQRCodeImage(payUrl, width, height, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderCode(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }

        if (order.getOrderStatus().equals(OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByOrderCode(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }

        if (order.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderCode(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }

        User user = UserFilter.currentUser;
        boolean checkAdminRole = userService.checkAdminRole(user);
        // normal customer can only finish his/her own order
        if (!checkAdminRole && !order.getUserId().equals(user.getId())) {
            throw new MallException(MallExceptionEnum.NOT_YOUR_ORDER);
        }

        if (order.getOrderStatus().equals(OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
