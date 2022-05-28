package com.cskaoyan.order.mq.consumer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.cskaoyan.order.constant.OrderConstants;
import com.cskaoyan.order.dal.entitys.Order;
import com.cskaoyan.order.dal.entitys.Stock;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cskaoyan.order.dal.entitys.OrderItem;
import com.cskaoyan.order.dal.persistence.OrderItemMapper;
import com.cskaoyan.order.dal.persistence.OrderMapper;
import com.cskaoyan.order.dal.persistence.StockMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Transactional
public class DelayOrderCancelConsumer {

    private DefaultMQPushConsumer mqConsumer;

    private String addr;
    private String topicName;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    StockMapper stockMapper;

    @PostConstruct
    public void init() throws MQClientException {
        log.info("NewOrderCancelConsumer ->初始化...,topic:{},addre:{} ",topicName, addr);
        mqConsumer = new DefaultMQPushConsumer("delay_order_consumer_group");
        mqConsumer.setNamesrvAddr("127.0.0.1:9876");
        mqConsumer.subscribe("delay_order_cancel", "*");

        MessageListenerConcurrently listener = (msgs, context) -> {
            log.info("开始执行订单[{}]的支付超时订单关闭......", context);
            // 查询订单状态
            String orderId = new String(msgs.get(0).getBody());
            Order order = new Order();
            order.setOrderId(orderId);

            try {
                Order unpayedOrder = orderMapper.selectByPrimaryKey(order);
                if (unpayedOrder == null || unpayedOrder.getStatus() != OrderConstants.ORDER_STATUS_INIT) {
                    return  ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                // 否则该订单还未成功付款，则修改订单状态，修改库存，修改中的商品条目状态
                unpayedOrder.setStatus(OrderConstants.ORDER_STATUS_TRANSACTION_CLOSE);
                orderMapper.updateByPrimaryKey(unpayedOrder);

                //将订单商品的库存状态改为释放
                orderItemMapper.updateStockStatus(2, orderId);

                // 修改库存
                List<OrderItem> orderItems = orderItemMapper.queryByOrderId(orderId);
                List<Long> itemIds = orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList());

                List<Stock> stocksForUpdate = stockMapper.findStocksForUpdate(itemIds);
                stocksForUpdate.stream().forEach(stock -> {
                    Optional<OrderItem> item = orderItems.stream()
                            .filter(orderItem -> stock.getItemId().equals(orderItem.getItemId()))
                            .findAny();

                    item.ifPresent(one -> {
                        stock.setLockCount(-item.get().getNum());
                        stock.setStockCount(item.get().getNum().longValue());
                        stockMapper.updateStock(stock);
                    });
                });

                log.info("超时订单{}处理完毕",orderId);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("超时订单处理失败:{}",orderId);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

        };

        mqConsumer.registerMessageListener(listener);
        log.info("register consumer NewOrderCancelConsumer success!");
        mqConsumer.start();
    }
}
