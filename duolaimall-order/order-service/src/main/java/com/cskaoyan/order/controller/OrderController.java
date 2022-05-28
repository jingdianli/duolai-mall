package com.cskaoyan.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.order.constant.OrderRetCode;
import com.cskaoyan.order.dto.*;
import com.cskaoyan.order.form.CancelOrderForm;
import com.cskaoyan.order.form.OrderDetail;
import com.cskaoyan.order.form.PageInfo;
import com.cskaoyan.order.form.PageResponse;
import com.cskaoyan.order.service.OrderCoreService;
import com.cskaoyan.order.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author Jingdian Li
 * @since 2022/05/28 04:58
 */

@RestController
@RequestMapping("/shopping")
@Slf4j
public class OrderController {
    @Autowired
    OrderCoreService orderCoreService;

    @Autowired
    OrderQueryService orderQueryService;
    /**
     * 创建订单
     *
     * @param createOrderRequest
     * @param servletRequest
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/28 5:02
    */
    @PostMapping("/order")
    public ResponseData addOrder(@RequestBody CreateOrderRequest createOrderRequest, HttpServletRequest servletRequest) {
        String userInfo = (String) servletRequest.getHeader("user_info");
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());
        createOrderRequest.setUserId(uid);
        createOrderRequest.setUniqueKey(UUID.randomUUID().toString());
        log.info("to add order " + createOrderRequest);
        CreateOrderResponse orderResponse = orderCoreService.createOrder(createOrderRequest);
        System.out.println();
        if (orderResponse.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(orderResponse.getOrderId());
        }
        return new ResponseUtil<>().setErrorMsg(orderResponse.getMsg());
    }

    /**
     * 获取当前用户的所有订单
     *
     * @param pageInfo
     * @param request
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/28 5:03
    */
    @GetMapping("/order")
    public ResponseData userOrders(PageInfo pageInfo, HttpServletRequest request) {

        OrderListRequest orderListRequest = new OrderListRequest();
        orderListRequest.setPage(pageInfo.getPage());
        orderListRequest.setSize(pageInfo.getSize());
        orderListRequest.setSort(pageInfo.getSort());

        String token = (String) request.getHeader("user_info");
        JSONObject tokenJson = JSON.parseObject(token);
        Long uid = tokenJson.getLong("uid");
        orderListRequest.setUserId(uid);

        OrderListResponse orderListResponse = orderQueryService.orderList(orderListRequest);
        if (orderListResponse.getCode().equals(OrderRetCode.SUCCESS.getCode())) {

            PageResponse pageResponse = new PageResponse();
            pageResponse.setData(orderListResponse.getDetailInfoList());
            pageResponse.setTotal(orderListResponse.getTotal());
            return new ResponseUtil<>().setData(pageResponse);
        }
        return new ResponseUtil<>().setErrorMsg(orderListResponse.getMsg());
    }

    /**
     * 查询订单详情
     *
     * @param orderId
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/28 5:03
    */
    @GetMapping("/order/{id}")
    public ResponseData orderDetail(@PathVariable("id") String orderId) {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setOrderId(orderId);

        OrderDetailResponse orderDetailResponse = orderQueryService.orderDetail(orderDetailRequest);
        if (orderDetailResponse.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderTotal(orderDetailResponse.getPayment());
            orderDetail.setUserId(orderDetailResponse.getUserId());
            orderDetail.setUserName(orderDetailResponse.getBuyerNick());
            orderDetail.setGoodsList(orderDetailResponse.getOrderItemDto());
            orderDetail.setTel(orderDetailResponse.getOrderShippingDto().getReceiverPhone());
            orderDetail.setStreetName(orderDetailResponse.getOrderShippingDto().getReceiverAddress());
            orderDetail.setOrderStatus(orderDetailResponse.getStatus());
            return new ResponseUtil<>().setData(orderDetail);
        }
        return new ResponseUtil<>().setErrorMsg(orderDetailResponse.getMsg());
    }

    /**
     * 取消订单
     *
     * @param cancelOrderForm
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/28 5:03
    */
    @PostMapping("/cancelOrder")
    public ResponseData cancelOrder(@RequestBody CancelOrderForm cancelOrderForm) {
        log.info("enter cancelOrder id = {}", cancelOrderForm.getOrderId());
        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setOrderId(cancelOrderForm.getOrderId());
        CancelOrderResponse cancelOrderResponse
                = orderCoreService.cancelOrder(cancelOrderRequest);
        return new ResponseUtil<>().setData(cancelOrderResponse);
    }

    /**
     * 删除订单
     *
     * @param id
     * @return
     */
    @DeleteMapping("/order/{id}")
    public ResponseData deleteOrder(@PathVariable String id) {
        return null;
    }
}
