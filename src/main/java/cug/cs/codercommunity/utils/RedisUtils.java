package cug.cs.codercommunity.utils;

import cug.cs.codercommunity.enums.LikeStatusEnum;
import cug.cs.codercommunity.enums.RedisKeyEnum;
import cug.cs.codercommunity.enums.UpdateScoreTypeEnum;
import cug.cs.codercommunity.mapper.CommentLikeMapper;
import cug.cs.codercommunity.mapper.CommentMapper;
import cug.cs.codercommunity.mapper.QuestionLikeMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @program: codercommunity
 * @description: redis工具封装类
 * @author: Sakura
 * @create: 2021-12-09 20:21
 **/

@Slf4j
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionLikeMapper questionLikeMapper;
    @Autowired
    private CommentLikeMapper commentLikeMapper;


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
            Question question = questionMapper.selectById(questionId);
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_TITLE.getKey(), String.valueOf(questionId), question.getTitle());
            redisTemplate.opsForZSet().add(
                    RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(),
                    String.valueOf(questionId),
                    question.getViewCount() + question.getLikeCount() * 2 + question.getCommentCount() * 3
            );
        }
    }

    /*
     * @Author sakura
     * @Description 从redis中获取某个问题点赞状态，如果获取不到，从数据库中查询并设置到缓存中
     * @Date 2021/12/10
     * @Param [userId, questionId]
     * @return java.lang.Integer
     **/
    public Integer getAndSetQuestionLikeStatus(Integer userId, Integer questionId){
        Object redisStatus = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), userId + ":" + questionId);
        if (redisStatus == null){
            Integer dbStatus = questionLikeMapper.selectStatus(userId, questionId);
            if (dbStatus == null){
                dbStatus = LikeStatusEnum.UNLIKE.getStatus();
            }
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), userId + ":" + questionId, dbStatus);
            return dbStatus;
        }
        else {
            return (Integer) redisStatus;
        }
    }

    /*
     * @Author sakura
     * @Description 从redis中获取某条评论点赞状态，如果获取不到，从数据库中查询并设置到缓存中
     * @Date 2021/12/10
     * @Param [userId, commentId]
     * @return java.lang.Integer
     **/
    public Integer getAndSetCommentLikeStatus(Integer userId, Integer commentId){
        Object redisStatus = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), userId + ":" + commentId);
        if (redisStatus == null){
            Integer dbStatus = commentLikeMapper.selectStatus(userId, commentId);
            if (dbStatus == null){
                dbStatus = LikeStatusEnum.UNLIKE.getStatus();
            }
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), userId + ":" + commentId, dbStatus);
            return dbStatus;
        }
        else {
            return (Integer) redisStatus;
        }
    }

    /*
     * @Author sakura
     * @Description 从redis中获取某个问题的最新点赞数，如果获取不到，将数据库中查到的最新值加载到缓存中
     * @Date 2021/12/10
     * @Param [questionId, dbLikeCount]
     * @return java.lang.Integer
     **/
    public Integer getAndSetQuestionLikeCount(Integer questionId, Integer dbLikeCount){
        Object likeCount = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_QUESTION_LIKE_COUNT.getKey(), String.valueOf(questionId));
        if (likeCount == null){
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_LIKE_COUNT.getKey(), String.valueOf(questionId), dbLikeCount);
            return dbLikeCount;
        }
        else {
            return (Integer) likeCount;
        }
    }

    /*
     * @Author sakura
     * @Description 从redis中获取某条评论的最新点赞数，如果获取不到，将数据库中查到的最新值加载到缓存中
     * @Date 2021/12/10
     * @Param [questionId, dbLikeCount]
     * @return java.lang.Integer
     **/
    public Integer getAndSetCommentLikeCount(Integer commentId, Integer dbLikeCount){
        Object likeCount = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT.getKey(), String.valueOf(commentId));
        if (likeCount == null){
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT.getKey(), String.valueOf(commentId), dbLikeCount);
            return dbLikeCount;
        }
        else {
            return (Integer) likeCount;
        }
    }

    public void incrementQuestionViewCount(Integer questionId){
        Boolean hasKey = redisTemplate.opsForHash().hasKey(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), String.valueOf(questionId));
        if (Boolean.FALSE.equals(hasKey)){
            Integer dbCount = questionMapper.selectViewCount(questionId);
            if (dbCount == null){
                dbCount = 0;
            }
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), String.valueOf(questionId), dbCount);
        }
        redisTemplate.opsForHash().increment(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), String.valueOf(questionId), 1L);
    }


    public Integer getAndSetQuestionViewCount(Integer questionId, Integer dbViewCount){
        Object viewCount = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), String.valueOf(questionId));
        if (viewCount == null){
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), String.valueOf(questionId), dbViewCount);
            return dbViewCount;
        }
        else {
            return (Integer) viewCount;
        }
    }


    /*
     * @Author sakura
     * @Description 根据当前问题点赞状态去更新redis中的点赞状态
     * @Date 2021/12/10
     * @Param [userId, questionId, currentStatus]
     * @return void
     **/
    public void updateQuestionLikeStatus(Integer userId, Integer questionId, LikeStatusEnum currentStatus){
        String hashKey = userId + ":" + questionId;
        Integer redisStatus = (Integer) redisTemplate.opsForHash().get(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), hashKey);
        //如果传递过来状态redis中状态与传递过来的状态不一致，说明不需要更新
        if (redisStatus != null && !redisStatus.equals(currentStatus.getStatus())){
            return;
        }
        //更新redis中的点赞状态和点赞计数
        if (LikeStatusEnum.UNLIKE == currentStatus){
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), hashKey, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment(RedisKeyEnum.MAP_QUESTION_LIKE_COUNT.getKey(), String.valueOf(questionId), 1L);
            //点赞，更新问题热度
            updateQuestionScoreByType(questionId, UpdateScoreTypeEnum.LIKE);
        }else {
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), hashKey, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment(RedisKeyEnum.MAP_QUESTION_LIKE_COUNT.getKey(), String.valueOf(questionId), -1L);
            //取消赞，更新问题热度
            updateQuestionScoreByType(questionId, UpdateScoreTypeEnum.UNLIKE);
        }
    }

    public void updateCommentLikeStatus(Integer userId, Integer commentId, LikeStatusEnum currentStatus){
        String hashKey = userId + ":" + commentId;
        Integer redisStatus = (Integer) redisTemplate.opsForHash().get(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), hashKey);
        //如果传递过来状态redis中状态与传递过来的状态不一致，说明不需要更新
        if (redisStatus != null && !redisStatus.equals(currentStatus.getStatus())){
            return;
        }
        //更新redis中的点赞状态和点赞计数
        if (LikeStatusEnum.UNLIKE == currentStatus){
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), hashKey, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT.getKey(), String.valueOf(commentId), 1L);
        }else {
            redisTemplate.opsForHash().put(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), hashKey, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT.getKey(), String.valueOf(commentId), -1L);
        }
    }

    /*
     * @Author sakura
     * @Description 从redis中查询前10个热点问题，用于前端展示
     * @Date 2021/12/9
     * @Param []
     * @return java.util.List<cug.cs.codercommunity.vo.HotQuestionVO>
     **/
    public List<HotQuestionVO> getHotQuestionsFromRedis() {
        //从redis获取前10条热门问题的questionId
        Set<Object> questionIds = redisTemplate.opsForZSet().reverseRange(RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(), 0L, 10L);
        List<HotQuestionVO> res = new ArrayList<>();
        if (questionIds == null) return res;
        //获取title和热度score
        for (Object id : questionIds) {
            HotQuestionVO hotQuestionVO = new HotQuestionVO();
            hotQuestionVO.setId(Integer.valueOf((String) id));
            Object title = redisTemplate.opsForHash().get(RedisKeyEnum.MAP_QUESTION_TITLE.getKey(), String.valueOf(id));
            hotQuestionVO.setTitle((String) title);
            Double score = redisTemplate.opsForZSet().score(RedisKeyEnum.ZSET_QUESTION_SCORE.getKey(), id);
            hotQuestionVO.setScore(score != null ? score : 0);
            res.add(hotQuestionVO);
        }
        return res;
    }


    public Map<Object, Object> getAllKeyValues(RedisKeyEnum keyEnum){
        return redisTemplate.opsForHash().entries(keyEnum.getKey());
    }

    public Boolean delete(RedisKeyEnum keyEnum){
        return redisTemplate.delete(keyEnum.getKey());
    }

    public Boolean deleteNoUpdatedHashKeyByLua(String key, String hashKey, Integer value){
        RedisScript<Boolean> redisScript = RedisScript.of(new ClassPathResource("lua/deleteNoUpdatedHashKey.lua"), Boolean.class);
        return redisTemplate.execute(redisScript, Arrays.asList(key, hashKey), value);
    }
}
