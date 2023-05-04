package com.yuan.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author: yuanxiaolong
 * @Title: CustomProducer
 * @ProjectName: springboot-test
 * @Description:
 * @date: 2023/4/27 14:53
 */
public class CustomProducer {

    public void test() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");// 连接集群
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());// 序列化KEY
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);// 缓存区大小，默认32M
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);// 批次大小，默认16K
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 5);// 等待时间，默认0ms
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");// 压缩方式，可配置gzip、snappy、lz4、zstd
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");// 应答级别，默认-1 all
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);// 重试次数，默认int最大值
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);
        kafkaProducer.send(new ProducerRecord<>("first", "Hello Kafka"));// 发送数据
        kafkaProducer.close();// 关闭资源
    }

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

    /**
     * 指定分区
     *
     * @date 2023/5/4 15:52
     * @author yuanxiaolong
     */
    public void test3() {
        Properties properties = new Properties();// 属性配置
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);// 创建生产者
        kafkaProducer.send(new ProducerRecord<>("first", 0, "", "Hello Kafka"), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e == null) {
                    System.out.println("主题：" + recordMetadata.topic() + " 分区：" + recordMetadata.partition());
                }
            }
        });// 发送数据
        kafkaProducer.close();// 关闭资源
    }

    /**
     * 自定义分区器
     *
     * @date 2023/5/4 15:52
     * @author yuanxiaolong
     */
    public void test4() {
        Properties properties = new Properties();// 属性配置
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);// 创建生产者
        kafkaProducer.send(new ProducerRecord<>("first", 0, "", "测试分区 Kafka"), new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e == null) {
                    System.out.println("主题：" + recordMetadata.topic() + " 分区：" + recordMetadata.partition());
                }
            }
        });// 发送数据
        kafkaProducer.close();// 关闭资源
    }

    /**
     * 同步发送消息
     *
     * @date 2023/5/4 15:02
     * @author yuanxiaolong
     */
    public void testSync() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();// 属性配置
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);// 创建生产者
        kafkaProducer.send(new ProducerRecord<>("first", "Hello Kafka")).get();// 同步发送数据
        kafkaProducer.close();// 关闭资源
    }

}
