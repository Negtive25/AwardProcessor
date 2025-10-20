package org.com.code.certificateProcessor.rocketMQ.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import org.com.code.certificateProcessor.rocketMQ.MQConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    private static final int SYNC_SEND_MAX_RETRY_TIMES = 3;
    private static final int ASYNC_SEND_MAX_RETRY_TIMES = 3;

    @Bean("CustomizedTemplate")
    RocketMQTemplate producer() {
        // 创建 DefaultMQProducer 实例,同时绑定到一个自定义生产者组底下
        DefaultMQProducer producer = new DefaultMQProducer(MQConstants.Producer.SUBMISSION);
        // 指定这个生产者的 NameServer 地址
        producer.setNamesrvAddr(nameServer);

        // 设置消息发送失败时的最大重试次数
        producer.setRetryTimesWhenSendFailed(SYNC_SEND_MAX_RETRY_TIMES); // 最多重试3次

        producer.setRetryTimesWhenSendAsyncFailed(ASYNC_SEND_MAX_RETRY_TIMES);
        // 将 producer 绑定到 RocketMQTemplate
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(producer);

        return template;
    }
}
