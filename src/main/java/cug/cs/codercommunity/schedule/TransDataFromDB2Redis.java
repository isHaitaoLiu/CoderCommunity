package cug.cs.codercommunity.schedule;


import cug.cs.codercommunity.enums.RedisKeyEnum;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransDataFromDB2Redis {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void transHotQuestionFromDB2Redis(){
        int offset = 0;
        int limit = 10;
        log.info("======= 热门问题加载到redis, 开始 ======");
        //删除已经存在与redis中的热门问题数据
        redisTemplate.delete(RedisKeyEnum.ZSET_QUESTION_SCORE.getKey());
        redisTemplate.delete(RedisKeyEnum.MAP_QUESTION_TITLE.getKey());

        //数据库搜索前10条热榜数据
        List<Question> questionList = questionMapper.selectHotQuestions(offset, limit);
        for (Question question: questionList) {
            redisTemplate.opsForZSet().add(
                    RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(),
                    String.valueOf(question.getId()),
                    question.getViewCount() + question.getLikeCount() * 2 + question.getCommentCount() * 3
            );
            redisTemplate.opsForHash().put(
                    RedisKeyEnum.MAP_QUESTION_TITLE.getKey(),
                    String.valueOf(question.getId()),
                    question.getTitle());
        }
        log.info("======= 热门问题加载到redis，完成，加载数量 : {} ======", questionList.size());
    }
}
