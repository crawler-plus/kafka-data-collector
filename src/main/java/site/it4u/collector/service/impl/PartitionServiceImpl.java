package site.it4u.collector.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.it4u.collector.entity.Partition;
import site.it4u.collector.repository.PartitionRepository;
import site.it4u.collector.service.PartitionService;

import java.util.List;

@Service
public class PartitionServiceImpl implements PartitionService {

    @Autowired
    private PartitionRepository partitionRepository;

    @Override
    public void save(Partition partition) {
        partitionRepository.save(partition);
    }

    @Override
    public List<Object[]> getDetailPartitionInfoByConsumerGroupAndTopic(String consumerGroupNameTopicName) {
        return partitionRepository.getDetailPartitionInfoByConsumerGroupAndTopic(consumerGroupNameTopicName);
    }

    @Override
    @Transactional
    public void removeOldData(Long dataGenerateTime) {
        partitionRepository.removeOldData(dataGenerateTime);
    }
}
