package site.it4u.collector.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * consumerGroup
 */
@Entity(name = "t_consumer_group")
@Getter
@Setter
public class ConsumerGroup {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "consumer_group_name")
    private String name;
}
