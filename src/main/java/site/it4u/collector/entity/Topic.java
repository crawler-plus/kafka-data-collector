package site.it4u.collector.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * topic
 */
@Entity(name = "t_topic")
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "topic_name")
    private String name;

    @Column(name = "topic_consumer_group_name")
    private String consumerGroupName;
}
