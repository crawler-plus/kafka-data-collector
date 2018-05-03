package site.it4u.collector.utils;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@AllArgsConstructor
public class KafkaUtils {

    private String topic;

    private int port;

    private String host;

    private int time;

    public Map<String, String> getEveryPartitionMaxOffset() {
        TreeMap<Integer, PartitionMetadata> partitionIdAndMeta = findTopicEveryPartition();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Integer, PartitionMetadata> entry : partitionIdAndMeta.entrySet()) {
            int leaderPartitionId = entry.getKey();
            String leadBroker = entry.getValue().leader().host();
            String clientName = "Client_" + topic + "_" + leaderPartitionId;
            SimpleConsumer consumer = new SimpleConsumer(leadBroker, port,100000, 64 * 1024, clientName);
            long readOffset = getLastOffset(consumer, topic, leaderPartitionId, clientName);
            map.put(String.valueOf(leaderPartitionId), String.valueOf(readOffset));
            if (consumer != null) {
                consumer.close();
            }
        }
        return map;
    }

    private TreeMap<Integer, PartitionMetadata> findTopicEveryPartition(){
        TreeMap<Integer, PartitionMetadata> map = new TreeMap<>();
        SimpleConsumer consumer = null;
        try {
            consumer = new SimpleConsumer(host, port, 100000, 64 * 1024,"leaderLookup" + new Date().getTime());
            List<String> topics = Collections.singletonList(topic);
            TopicMetadataRequest req = new TopicMetadataRequest(topics);
            kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
            List<TopicMetadata> metaData = resp.topicsMetadata();
            if(metaData!=null && !metaData.isEmpty()){
                TopicMetadata item = metaData.get(0);
                for (PartitionMetadata part : item.partitionsMetadata()) {
                    map.put(part.partitionId(), part);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
        return map;
    }

    private long getLastOffset(SimpleConsumer consumer, String topic,int leaderPartitionId, String clientName) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic,leaderPartitionId);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(time, 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(),clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if (response.hasError()) {
            log.error("error");
            return 0L;
        }
        long[] offsets = response.offsets(topic, leaderPartitionId);
        return offsets[0];
    }
}
