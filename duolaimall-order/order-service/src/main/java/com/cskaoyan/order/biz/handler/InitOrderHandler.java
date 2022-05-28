package com.cskaoyan.order.biz.handler;

import com.cskaoyan.mall.commons.exception.BizException;
import com.cskaoyan.mall.commons.util.NumberUtils;
import com.cskaoyan.order.biz.callback.SendEmailCallback;
import com.cskaoyan.order.biz.callback.TransCallback;
import com.cskaoyan.order.biz.context.CreateOrderContext;
import com.cskaoyan.order.biz.context.TransHandlerContext;
import com.cskaoyan.order.constant.OrderConstants;
import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.order.dal.entitys.Order;
import com.cskaoyan.order.dal.entitys.OrderItem;
import com.cskaoyan.order.dal.persistence.OrderItemMapper;
import com.cskaoyan.order.dal.persistence.OrderMapper;
import com.cskaoyan.order.utils.GlobalIdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 初始化订单 生成订单
 */

@Slf4j
@Component
public class InitOrderHandler extends AbstractTransHandler {
    @Autowired
    SendEmailCallback sendEmailCallback;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    GlobalIdGeneratorUtil globalIdGeneratorUtil;

    private final String ORDER_GLOBAL_ID_CACHE_KEY="ORDER_ID";
    private final String ORDER_ITEM_GLOBAL_ID_CACHE_KEY="ORDER_ITEM_ID";
    @Override
    public TransCallback getTransCallback() {
        return sendEmailCallback;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        log.info("begin InitOrderHandler :context:" + context);
        Order order = new Order();
        CreateOrderContext createOrderContext = (CreateOrderContext)context;

        // 保存订单
        String orderId = insertOrder(order, createOrderContext);

        // 保存订单item
        insertOrderItems(createOrderContext, orderId);
        return true;
    }

    private void insertOrderItems(CreateOrderContext createOrderContext, String orderId) {
        List<Long> buyProductIds=new ArrayList<>();
        try {
            createOrderContext.getCartProductDtoList().stream().forEach(cartProductDto -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(globalIdGeneratorUtil.getNextSeq(ORDER_ITEM_GLOBAL_ID_CACHE_KEY, 1));
                orderItem.setItemId(cartProductDto.getProductId());
                orderItem.setOrderId(String.valueOf(orderId));
                orderItem.setNum(Math.toIntExact(cartProductDto.getProductNum()));
                orderItem.setPrice(NumberUtils.toDouble(cartProductDto.getSalePrice()));
                orderItem.setTitle(cartProductDto.getProductName());
                orderItem.setPicPath(cartProductDto.getProductImg());
                orderItem.setTotalFee(cartProductDto.getSalePrice().multiply(BigDecimal.valueOf(cartProductDto.getProductNum())).doubleValue());
                buyProductIds.add(cartProductDto.getProductId());
                //已锁定库存
                log.info("to insert order item:" + orderItem);
                orderItem.setStatus(1);
                orderItemMapper.insert(orderItem);
            });
        } catch (Exception e){
            log.error("InitOrderHandler occur Exception :" + e);
            throw new BizException(OrderRetCode.DB_SAVE_EXCEPTION.getCode(), OrderRetCode.DB_SAVE_EXCEPTION.getMessage());
        }

        createOrderContext.setOrderId(orderId);
        createOrderContext.setBuyProductIds(buyProductIds);
    }

    private String insertOrder(Order order, CreateOrderContext createOrderContext) {
        try {
            String orderId = globalIdGeneratorUtil.getNextSeq(ORDER_GLOBAL_ID_CACHE_KEY, 1);
            order.setOrderId(orderId);
            order.setUserId(Long.valueOf(createOrderContext.getUserId()));
            order.setBuyerNick(createOrderContext.getBuyerNickName());
            order.setPayment(createOrderContext.getOrderTotal());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setStatus(OrderConstants.ORDER_STATUS_INIT);
            log.info("to insert order: " + order);
            orderMapper.insert(order); //保存订单
            return orderId;
        } catch(DuplicateKeyException e){
            log.error("订单重复提交："+e);
            throw new BizException(OrderRetCode.DB_SAVE_EXCEPTION.getCode(),OrderRetCode.DB_SAVE_EXCEPTION.getMessage());
        }catch (Exception e){
            log.error("InitOrderHandler occur Exception :"+e);
            throw new BizException(OrderRetCode.DB_SAVE_EXCEPTION.getCode(),OrderRetCode.DB_SAVE_EXCEPTION.getMessage());
        }
    }

}
