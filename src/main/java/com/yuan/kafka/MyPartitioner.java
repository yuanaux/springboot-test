package com.yuan.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * @author: yuanxiaolong
 * @Title: MyPartitioner
 * @ProjectName: springboot-test
 * @Description: 自定义分区器
 * @date: 2023/5/4 15:53
 */
public class MyPartitioner implements Partitioner {
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        // 自定义分区逻辑
        return o1.toString().contains("特殊字符") ? 0 : 1;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
