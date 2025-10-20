package org.com.code.certificateProcessor.rocketMQ.producer;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.com.code.certificateProcessor.exeption.RocketmqException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SubmissionProducer {
    @Autowired
    @Qualifier("CustomizedTemplate")
    private RocketMQTemplate producerTemplate;

    /**
     * 通用的异步发送消息方法
     */
    public void asyncSendMessage(Object content, String topic, String tag) {
        String msg = JSON.toJSONString(content);
        String destination = topic + ":" + tag;

        producerTemplate.asyncSend(destination, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                throw new RocketmqException("生产者发送消息失败,消息标签为:" + destination + ", 消息体为:" + msg,throwable);
            }
        });
    }
}
