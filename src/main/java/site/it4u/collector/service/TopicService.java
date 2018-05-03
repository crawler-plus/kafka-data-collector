package site.it4u.collector.service;

import site.it4u.collector.entity.Topic;

import java.util.List;

public interface TopicService {

    void save(Topic topic);

    boolean exists(String name, String consumerGroupName);

    List<String> getAllTopic();
}
