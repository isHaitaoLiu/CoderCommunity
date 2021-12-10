package cug.cs.codercommunity.utils;

import cug.cs.codercommunity.enums.RedisKeyEnum;
import cug.cs.codercommunity.enums.UpdateScoreTypeEnum;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: codercommunity
 * @description: redis工具封装类
 * @author: Sakura
 * @create: 2021-12-09 20:21
 **/

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QuestionMapper questionMapper;


    /*
     * @Author sakura
     * @Description 更新问题热度
     * @Date 2021/12/9
     * @Param [questionId, typeEnum]
     * @return void
     **/
    public void updateQuestionScoreByType(Integer questionId, UpdateScoreTypeEnum typeEnum){
        //查询当前问题是否存在于redis
        Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKeyEnum.MAP_QUESTION_TITLE.getKey(), String.valueOf(questionId));
        if (Boolean.TRUE.equals(hasKey)){
            //如果存在，直接更新分数
            redisTemplate.opsForZSet().incrementScore(RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(), String.valueOf(questionId), typeEnum.getType().doubleValue());
        }else {
            //如果不存在，从数据库中检索，并且添加到redis
            Question question = questionMapper.selectQuestionById(questionId);
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_TITLE.getKey(), String.valueOf(questionId), question.getTitle());
            redisTemplate.opsForZSet().add(
                    RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(),
                    String.valueOf(questionId),
                    question.getViewCount() + question.getLikeCount() * 2 + question.getCommentCount() * 3
            );
        }
    }


}
