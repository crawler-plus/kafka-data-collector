package site.it4u.collector.service;

import site.it4u.collector.entity.ConsumerGroup;

public interface ConsumerGroupService {

    void save(ConsumerGroup consumerGroup);

    boolean exists(String name);
}
