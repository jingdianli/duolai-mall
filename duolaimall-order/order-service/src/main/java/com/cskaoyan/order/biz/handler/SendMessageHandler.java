package com.cskaoyan.order.biz.handler;

import com.cskaoyan.order.biz.context.CreateOrderContext;
import com.cskaoyan.order.biz.context.TransHandlerContext;
import com.cskaoyan.order.mq.producer.DelayOrderCancelProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 利用mq发送延迟取消订单消息
 **/
@Component
@Slf4j
public class SendMessageHandler extends AbstractTransHandler {
	@Autowired
	private DelayOrderCancelProducer producer;

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	public boolean handle(TransHandlerContext context) {
		log.info("enter SendMessageHandler start send message...");
		CreateOrderContext createOrderContext = (CreateOrderContext) context;
		try {
			producer.sendOrderMessage(createOrderContext.getOrderId());
		} catch (Exception e) {
			log.error("发送订单id:{}到延迟队列失败",createOrderContext.getOrderId());
			return false;
		}

		return true;
	}
}