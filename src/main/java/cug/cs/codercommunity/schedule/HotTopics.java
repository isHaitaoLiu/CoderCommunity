package cug.cs.codercommunity.schedule;


import cug.cs.codercommunity.cache.HotTopicCache;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class HotTopics {

    @Autowired
    private HotTopicCache hotTopicCache;

    @Autowired
    private QuestionMapper questionMapper;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void hotTopicSchedule(){
        int offset = 0;
        int limit = 20;
        log.info("======= 热门问题统计定时任务开始，时间：{} ======", new Date());
        List<Question> list = new ArrayList<>();
        Map<String, Integer> priorities = new HashMap<>();
        while (offset == 0 || list.size() == limit){
            list = questionMapper.selectOnePage(offset, limit);
            for (Question question : list) {
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags) {
                    Integer priority = priorities.get(tag);
                    if (priority != null){
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    }
                    else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
        hotTopicCache.setTags(priorities);
        hotTopicCache.updateTop();
        log.info("======= 热门问题统计定时任务结束，时间：{} ======", new Date());
    }
}
