package com.hz.learnboot.rabbitmq.topic;

import com.hz.learnboot.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 主题模式 - 消息消费者1
 *
 * Created by hezhao on 2018-07-26 08:57
 */
public class TopicCustomer2 {

    private final static String EXCHANGE_NAME = "topicExchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 1.获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 2.创建通道
        Channel channel = connection.createChannel();
        // 3.申明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 4.声明一个临时队列
        String queueName = channel.queueDeclare().getQueue();

        // 绑定路由，同一个队列可以绑定多个值
        channel.queueBind(queueName,EXCHANGE_NAME,"key.#"); // 使用key.# 匹配所有的以key开头的

        // 5.消费消息
        channel.basicQos(1); // 统一时刻服务器只会发一条消息给消费者
        // 监听队列，手动返回完成
        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("主题模式 消费者2接收:" + message);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

}
