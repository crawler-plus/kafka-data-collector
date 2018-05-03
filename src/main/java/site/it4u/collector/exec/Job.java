package site.it4u.collector.exec;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import site.it4u.collector.conf.KafkaProperties;
import site.it4u.collector.conf.ZKProperties;
import site.it4u.collector.entity.ConsumerGroup;
import site.it4u.collector.entity.Partition;
import site.it4u.collector.entity.Topic;
import site.it4u.collector.service.ConsumerGroupService;
import site.it4u.collector.service.PartitionService;
import site.it4u.collector.service.TopicService;
import site.it4u.collector.utils.CacheUtils;
import site.it4u.collector.utils.KafkaUtils;
import site.it4u.collector.utils.ZKUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * batch job
 */
@Configuration
@EnableScheduling
@Slf4j
public class Job {

    @Autowired
    private ZKProperties zkProperties;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private ConsumerGroupService consumerGroupService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionService partitionService;

    private static final String CONSUMER_GROUP_PATH = "/queue/consumers";

    private static final String OFFSET_STR = "offsets";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * execute every 5 minutes
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cronJob() throws Exception {
        long currentMills = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        ZKUtils zkUtils = new ZKUtils(zkProperties.getHost() + ":" + zkProperties.getPort());
        CuratorFramework curatorFramework = zkUtils.buildCuratorFramework();
        curatorFramework.start(); // connect to zk
        List<String> children = curatorFramework.getChildren().forPath(CONSUMER_GROUP_PATH); // get all consumerGroup nodes
        CountDownLatch countDownLatch = new CountDownLatch(children.size());
        for(String child : children) {
            new Thread(() -> {
                try {
                    // check if consumerGroup node exists
                    boolean exists = consumerGroupService.exists(child);
                    if(!exists) {
                        // insert data into t_consumer_group
                        ConsumerGroup consumerGroup = new ConsumerGroup();
                        consumerGroup.setName(child);
                        consumerGroupService.save(consumerGroup);
                    }
                    List<String> eachConsumerGroupNode = curatorFramework.getChildren().forPath(CONSUMER_GROUP_PATH + "/" + child);
                    for(String eachC : eachConsumerGroupNode) {
                        if(OFFSET_STR.equals(eachC)) {
                            List<String> topics = curatorFramework.getChildren().forPath(CONSUMER_GROUP_PATH + "/" + child + "/" + eachC);
                            for (String topic : topics) {
                                log.debug("topic: {}", child + "_" + topic);
                                boolean checkTopicExists = topicService.exists(topic, child);
                                if(!checkTopicExists) {
                                    // insert data into t_topic
                                    Topic insertTopic = new Topic();
                                    insertTopic.setName(topic);
                                    insertTopic.setConsumerGroupName(child);
                                    topicService.save(insertTopic);
                                }
                                // -----------get the relationShip between partition and logsize for this topic----------- //
                                KafkaUtils offsetSearch = new KafkaUtils(topic, kafkaProperties.getPort(),kafkaProperties.getHost(), -1);
                                Map<String, String> map = offsetSearch.getEveryPartitionMaxOffset();
                                List<String> partitionsOfEachTopic = curatorFramework.getChildren().forPath(CONSUMER_GROUP_PATH + "/" + child + "/" + eachC + "/" + topic);
                                // for each partition
                                for(String partition : partitionsOfEachTopic) {
                                    int partitionNumber = Integer.valueOf(partition);
                                    Stat stat = new Stat();
                                    String eachStat = new String(curatorFramework.getData().storingStatIn(stat)
                                            .forPath(CONSUMER_GROUP_PATH + "/" + child + "/" + eachC + "/" + topic + "/" + partition));
                                    long offset = Long.valueOf(eachStat);
                                    long logSize = Long.valueOf(map.getOrDefault(String.valueOf(partitionNumber), String.valueOf(offset)));
                                    long lag = logSize - offset;
                                    Date dcDate = new Date(stat.getCtime());
                                    Date dmDate = new Date(stat.getMtime());
                                    String formatDCDate = simpleDateFormat.format(dcDate);
                                    String formatDMDate = simpleDateFormat.format(dmDate);
                                    log.debug("partition number: {}, partition offset: {}, logSize: {}, lag: {}, created time: {}, last seen time: {}",
                                            partitionNumber, offset, logSize, lag, formatDCDate, formatDMDate);
                                    // insert data into t_partition
                                    Partition insertPartition = new Partition();
                                    insertPartition.setNumber(partitionNumber);
                                    insertPartition.setTopicName(topic);
                                    insertPartition.setConsumerGroupName(child);
                                    insertPartition.setOffSize(offset);
                                    insertPartition.setLogSize(logSize);
                                    insertPartition.setLag(lag);
                                    insertPartition.setCreated(formatDCDate);
                                    insertPartition.setLastSeen(formatDMDate);
                                    insertPartition.setDataGenerateTime(currentMills);
                                    partitionService.save(insertPartition);// batch insert data into t_partition
                                }
                            }
                        }
                    }
                }catch (Exception e) {
                    log.error(e.getMessage());
                }finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        CacheUtils.getInstance().clearCache(); // clear all elements in cache
        curatorFramework.close();
    }

    /**
     * execute every 30 minutes
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void cronRemoveJob() {
        // delete data for 3 days ago
        Long now = System.currentTimeMillis();
        Long intervalMills = 1000 * 60 * 60 * 24 * 3L;
        Long pastMills = now - intervalMills;
        partitionService.removeOldData(pastMills);
    }
}
