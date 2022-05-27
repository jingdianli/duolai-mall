package com.cskaoyan.order.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.order.dto.*;
import com.cskaoyan.order.form.CancelOrderForm;
import com.cskaoyan.order.form.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jingdian Li
 * @since 2022/05/28 04:58
 */

public class OrderController {
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
        return null;
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

        return null;
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
        return null;
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
        return null;
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
