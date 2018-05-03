package site.it4u.collector.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.it4u.collector.entity.ConsumerGroup;
import site.it4u.collector.repository.ConsumerGroupRepository;
import site.it4u.collector.service.ConsumerGroupService;

@Service
public class ConsumerGroupServiceImpl implements ConsumerGroupService {

    @Autowired
    private ConsumerGroupRepository consumerGroupRepository;

    @Override
    public void save(ConsumerGroup consumerGroup) {
        consumerGroupRepository.save(consumerGroup);
    }

    @Override
    public boolean exists(String name) {
        return consumerGroupRepository.existsCount(name) > 0 ? true : false;
    }
}
