package com.cskaoyan.order.mq.producer;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DelayOrderCancelProducer {

    private DefaultMQProducer mqProducer;

    private String addr;
    private String topicName;
    // 消息延迟级别
    //private static final Integer delayLevel = 14;
    // 测试用30s
    private static final Integer delayLevel = 4;

    @PostConstruct
    public void init(){

        log.info("mqProducer ->初始化...addr:{}, topicName:{}",addr,topicName);
        mqProducer = new DefaultMQProducer("delay_order_producer_group");
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        try {
            mqProducer.start();
        } catch (MQClientException e) {
            log.info("初始化rocketMQ失败……addr:{}",addr);
            e.printStackTrace();
        }

    }

    //发送订单延迟消息
    public void sendOrderMessage(String context) {
        Message message = new Message("delay_order_cancel",context.getBytes(Charset.forName("utf-8")));
        //延迟十分钟
//        messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        message.setDelayTimeLevel(delayLevel);
        SendResult sendResult = null;
        try {
            log.info("start send order delayed message: " + message);
            sendResult = mqProducer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("订单消息发送失败…context:{}",context);
        } catch (RemotingException e) {
            e.printStackTrace();
            log.info("订单消息发送失败…context:{}",context);
        } catch (MQBrokerException e) {
            e.printStackTrace();
            log.info("订单消息发送失败…context:{}",context);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("订单消息发送失败…context:{}",context);
        }
        if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
            log.info("延迟取消订单消息发送成功…context:{}",context);
            return;
        }else {
            log.info("订单消息发送失败…context:{}",context);
        }
    }
}
