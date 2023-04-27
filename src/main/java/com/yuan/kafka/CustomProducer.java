package com.yuan.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @author: yuanxiaolong
 * @Title: CustomProducer
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2023/4/27 14:53
 */
public class CustomProducer {

    public void test1() {
        Properties properties = new Properties();// 属性配置
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);// 创建生产者
        kafkaProducer.send(new ProducerRecord<>("first", "Hello Kafka"));// 发送数据
        kafkaProducer.close();// 关闭资源
    }

    public void test2() {
        Properties properties = new Properties();// 属性配置
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);// 创建生产者
        kafkaProducer.send(new ProducerRecord<>("first", "Hello Kafka"), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e == null) {
                    System.out.println("主题：" + recordMetadata.topic() + " 分区：" + recordMetadata.partition());
                }
            }
        });// 发送数据
        kafkaProducer.close();// 关闭资源
    }

}
