package site.it4u.collector.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * partition
 */
@Entity(name = "t_partition")
@Getter
@Setter
public class Partition {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "partition_number")
    private Integer number;

    @Column(name = "partition_topic_name")
    private String topicName ;

    @Column(name = "partition_topic_consumer_group_name")
    private String consumerGroupName;

    @Column(name = "partition_offSize")
    private Long offSize;

    @Column(name = "partition_logSize")
    private Long logSize;

    @Column(name = "partition_lag")
    private Long lag;

    @Column(name = "partition_created")
    private String created;

    @Column(name = "partition_lastSeen")
    private String lastSeen;

    @Column(name = "data_generate_time")
    private Long dataGenerateTime;
}
