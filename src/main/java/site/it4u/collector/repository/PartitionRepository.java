package site.it4u.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.it4u.collector.entity.Partition;

import java.util.List;

@Repository
public interface PartitionRepository extends JpaRepository<Partition, Integer> {

    @Query("select concat(consumerGroupName, '_', topicName), dataGenerateTime , sum(offSize), sum(logSize), sum(lag)" +
            " from t_partition where concat(consumerGroupName, '_', topicName) = ?1 group by dataGenerateTime order by dataGenerateTime")
    List<Object[]> getDetailPartitionInfoByConsumerGroupAndTopic(String consumerGroupNameTopicName);

    @Modifying
    @Query("delete from t_partition where dataGenerateTime < ?1")
    void removeOldData(Long dataGenerateTime);
}
