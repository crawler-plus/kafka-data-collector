package site.it4u.collector.service;

import site.it4u.collector.entity.Partition;

import java.util.List;

public interface PartitionService {

    void save(Partition partition);

    List<Object[]> getDetailPartitionInfoByConsumerGroupAndTopic(String consumerGroupNameTopicName);

    void removeOldData(Long dataGenerateTime);
}
