package com.cskaoyan.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.mall.order.dto.PayOrderSuccessRequest;
import com.cskaoyan.mall.order.dto.PayOrderSuccessResponse;
import com.cskaoyan.order.biz.TransOutboundInvoker;
import com.cskaoyan.order.biz.context.AbsTransHandlerContext;
import com.cskaoyan.order.biz.factory.OrderProcessPipelineFactory;
import com.cskaoyan.order.constant.OrderConstants;
import com.cskaoyan.order.dal.entitys.Order;
import com.cskaoyan.order.dal.persistence.OrderMapper;
import com.cskaoyan.order.dto.*;
import com.cskaoyan.order.service.OrderCoreService;
import com.cskaoyan.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Jingdian Li
 * @since 2022/05/28 05:07
 */

@Service
@Slf4j
public class OrderCoreServiceImpl implements OrderCoreService {
    @Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;

    @Autowired
    OrderMapper orderMapper;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        CreateOrderResponse response = new CreateOrderResponse();
        try {
            //创建pipeline对象
            TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

            //启动pipeline
            invoker.start(); //启动流程（pipeline来处理）

            //获取处理结果
            AbsTransHandlerContext context = invoker.getContext();

            //把处理结果转换为response
            response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);

        } catch (Exception e) {
            log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public CancelOrderResponse cancelOrder(CancelOrderRequest request) {


        CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();

        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setStatus(OrderConstants.ORDER_STATUS_TRANSACTION_CANCEL);
        order.setCloseTime(new Date());

        try {
            request.requestCheck();
            log.info("修改訂單狀態 order:{}", JSON.toJSONString(order));
            int num = orderMapper.updateByPrimaryKeySelective(order);
            log.info("cancelOrder,effect Row:" + num);
            cancelOrderResponse.setCode(OrderRetCode.SUCCESS.getCode());
            cancelOrderResponse.setMsg(OrderRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("OrderCoreServiceImpl.cancelOrder Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(cancelOrderResponse, e);
        }
        return cancelOrderResponse;
    }

    @Override
    public DeleteOrderResponse deleteOrder(DeleteOrderRequest request) {
        return null;
    }

    @Override
    public void updateOrder(Integer status, String orderId) {

    }

    @Override
    public void deleteOrderWithTransaction(DeleteOrderRequest request) {

    }

    @Override
    public PayOrderSuccessResponse payOrderSuccess(PayOrderSuccessRequest request) {
        return null;
    }
}
