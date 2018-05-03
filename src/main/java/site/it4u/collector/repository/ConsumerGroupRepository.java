package site.it4u.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.it4u.collector.entity.ConsumerGroup;

@Repository
public interface ConsumerGroupRepository extends JpaRepository<ConsumerGroup, Integer> {

    @Query("select count(id) from t_consumer_group t where t.name = ?1")
    int existsCount(String name);

}
