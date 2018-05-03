package site.it4u.collector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.it4u.collector.entity.*;
import site.it4u.collector.service.PartitionService;
import site.it4u.collector.service.TopicService;
import site.it4u.collector.utils.CacheUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class KafkaLogController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/", method = {GET, POST})
    public Map<String, Object> forConnection() {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("status", 200);
        retMap.put("msg", "OK");
        return retMap;
    }

    @RequestMapping(value = "/search", method = {GET, POST})
    public String search() throws Exception {
        String topicListStr = CacheUtils.getInstance().getDataFromCache("topicListStr");
        if(StringUtils.isEmpty(topicListStr)) {
            List<String> allTopic = topicService.getAllTopic();
            String tlS = objectMapper.writeValueAsString(allTopic);
            CacheUtils.getInstance().putIntoCache("topicListStr", tlS);
            return tlS;
        }
        return topicListStr;
    }

    @RequestMapping(value = "/annotations", method = {GET, POST})
    public List<AnnotationEntity> annotations() {
        Annotation annotation = new Annotation("KafkaDataCollectorAnnotation", true, "KafkaDataSource", true);
        AnnotationEntity annotationEntity = new AnnotationEntity();
        annotationEntity.setAnnotation(annotation);
        annotationEntity.setTime(System.currentTimeMillis());
        annotationEntity.setTitle("KafkaAnnotation");
        List<AnnotationEntity> annotations = new ArrayList<>();
        annotations.add(annotationEntity);
        return annotations;
    }

    @RequestMapping(value = "/query", method = {GET, POST})
    public String query(@RequestBody QueryBody queryBody) throws Exception {
        List<Target> targetList = queryBody.getTargets();
        // get key for the cache
        String key = targetList.stream().map(t -> t.getTarget()).collect(Collectors.joining("_"));
        String cacheStr = CacheUtils.getInstance().getDataFromCache(key);
        if(StringUtils.isEmpty(cacheStr)) {
            List<QueryEntity> qList = new ArrayList<>();
            for (Target target : targetList) {
                List<Long[]> list = new ArrayList<>();
                // get the name of each target
                String tar = target.getTarget();
                String[] split = tar.split("_");
                String consumerGroupTopicStr = split[0] + "_" + split[1];
                List<Object[]> detailPartitionInfoByConsumerGroupAndTopic = partitionService.getDetailPartitionInfoByConsumerGroupAndTopic(consumerGroupTopicStr);
                if(!CollectionUtils.isEmpty(detailPartitionInfoByConsumerGroupAndTopic)) {
                    if(tar.endsWith("offset")) {
                        for (Object[] objects : detailPartitionInfoByConsumerGroupAndTopic) {
                            list.add(new Long[]{(Long) objects[2], (Long) objects[1]});
                        }
                    }else if(tar.endsWith("logsize")) {
                        for (Object[] objects : detailPartitionInfoByConsumerGroupAndTopic) {
                            list.add(new Long[]{(Long) objects[3], (Long) objects[1]});
                        }
                    }else {
                        for (Object[] objects : detailPartitionInfoByConsumerGroupAndTopic) {
                            list.add(new Long[]{(Long) objects[4], (Long) objects[1]});
                        }
                    }
                }
                QueryEntity q = new QueryEntity();
                q.setTarget(tar);
                q.setDatapoints(list);
                qList.add(q);
            }
            String tlS = objectMapper.writeValueAsString(qList);
            CacheUtils.getInstance().putIntoCache(key, tlS);
            return tlS;
        }
        return cacheStr;
    }
}
