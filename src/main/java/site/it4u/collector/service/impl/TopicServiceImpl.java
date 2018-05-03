package site.it4u.collector.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.it4u.collector.entity.Topic;
import site.it4u.collector.repository.TopicRepository;
import site.it4u.collector.service.TopicService;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Override
    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public boolean exists(String name, String consumerGroupName) {
        return topicRepository.existsCount(name, consumerGroupName) > 0 ? true : false;
    }

    @Override
    public List<String> getAllTopic() {
        return topicRepository.getAllTopic();
    }
}
