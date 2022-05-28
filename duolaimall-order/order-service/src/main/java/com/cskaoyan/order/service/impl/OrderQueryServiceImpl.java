package com.cskaoyan.order.service.impl;

import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.order.converter.OrderConverter;
import com.cskaoyan.order.dal.entitys.Order;
import com.cskaoyan.order.dal.entitys.OrderItem;
import com.cskaoyan.order.dal.entitys.OrderShipping;
import com.cskaoyan.order.dal.persistence.OrderItemMapper;
import com.cskaoyan.order.dal.persistence.OrderMapper;
import com.cskaoyan.order.dal.persistence.OrderShippingMapper;
import com.cskaoyan.order.dto.*;
import com.cskaoyan.order.service.OrderQueryService;
import com.cskaoyan.order.utils.ExceptionProcessorUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingdian Li
 * @since 2022/05/28 10:26
 */

@Service
@Slf4j
public class OrderQueryServiceImpl implements OrderQueryService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderConverter orderConverter;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Override
    public OrderCountResponse orderCount(OrderCountRequest request) {
        return null;
    }

    @Override
    public OrderListResponse orderList(OrderListRequest request) {
        request.requestCheck();

        OrderListResponse orderListResponse = new OrderListResponse();
        orderListResponse.setCode(OrderRetCode.SUCCESS.getCode());
        orderListResponse.setMsg(OrderRetCode.SUCCESS.getMessage());

        PageHelper.startPage(request.getPage(), request.getSize());
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("userId", request.getUserId());
        List<Order> orderList = orderMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(orderList)){
            orderListResponse.setTotal(0L);
            orderListResponse.setDetailInfoList(new ArrayList<>());
            return orderListResponse;
        }

        List<OrderDetailInfo> infos = new ArrayList<>();
        orderListResponse.setTotal((long)orderList.size());
        try {
            orderList.stream().forEach(order -> {
                OrderDetailInfo orderDetailInfo = getOrderDetailInfo(order);
                System.out.println(orderDetailInfo.getCreateTime().getTime());
                infos.add(orderDetailInfo);
            });

            orderListResponse.setDetailInfoList(infos);
        } catch (Exception e) {
            log.info("OrderQueryServiceImpl.orderList occur Exception: {}" , e);
            ExceptionProcessorUtils.wrapperHandlerException(orderListResponse, e);
        }
        return orderListResponse;
    }

    @Override
    public OrderDetailResponse orderDetail(OrderDetailRequest request) {
        request.requestCheck();

        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

        try {
            Order order = orderMapper.selectByPrimaryKey(request.getOrderId());
            orderDetailResponse = orderConverter.order2res(order);
            orderDetailResponse.setCode(OrderRetCode.SUCCESS.getCode());
            orderDetailResponse.setMsg(OrderRetCode.SUCCESS.getMessage());

            List<OrderItem> orderItems = orderItemMapper.queryByOrderId(order.getOrderId());
            List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
            orderDetailResponse.setOrderItemDto(orderItemDtos);

            OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(order.getOrderId());
            OrderShippingDto orderShippingDto = orderConverter.shipping2dto(orderShipping);
            orderDetailResponse.setOrderShippingDto(orderShippingDto);
        } catch (Exception e) {
            log.error("OrderQueryServiceImpl.orderDetail occur Exception :" +e);
            ExceptionProcessorUtils.wrapperHandlerException(orderDetailResponse, e);
        }

        return orderDetailResponse;
    }

    @Override
    public OrderItemResponse orderItem(OrderItemRequest request) {
        return null;
    }

    private OrderDetailInfo getOrderDetailInfo(Order order) {
        OrderDetailInfo orderDetailInfo = orderConverter.order2detail(order);


        List<OrderItem> orderItems = orderItemMapper.queryByOrderId(order.getOrderId());
        List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
        orderDetailInfo.setOrderItemDto(orderItemDtos);

        OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(order.getOrderId());
        OrderShippingDto orderShippingDto = orderConverter.shipping2dto(orderShipping);
        orderDetailInfo.setOrderShippingDto(orderShippingDto);
        return orderDetailInfo;
    }
}
