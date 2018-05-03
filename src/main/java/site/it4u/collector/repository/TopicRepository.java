package site.it4u.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.it4u.collector.entity.Topic;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

    @Query("select count(id) from t_topic t where t.name = ?1 and t.consumerGroupName = ?2")
    int existsCount(String name, String consumerGroupName);

    @Query(value = "select concat(topic_consumer_group_name, '_', topic_name, '_', 'offset') from t_topic " +
            "union all " +
            "select concat(topic_consumer_group_name, '_', topic_name, '_', 'logsize') from t_topic " +
            "union all " +
            "select concat(topic_consumer_group_name, '_', topic_name, '_', 'lag') from t_topic " +
            "order by 1 desc", nativeQuery = true)
    List<String> getAllTopic();
}
