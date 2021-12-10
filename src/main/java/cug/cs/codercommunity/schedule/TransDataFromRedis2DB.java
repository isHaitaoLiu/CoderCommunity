package cug.cs.codercommunity.schedule;

import cug.cs.codercommunity.enums.RedisKeyEnum;
import cug.cs.codercommunity.mapper.CommentLikeMapper;
import cug.cs.codercommunity.mapper.CommentMapper;
import cug.cs.codercommunity.mapper.QuestionLikeMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Comment;
import cug.cs.codercommunity.model.CommentLike;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.QuestionLike;
import cug.cs.codercommunity.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * @program: codercommunity
 * @description: 将点赞详情从redis中保存到数据库
 * @author: Sakura
 * @create: 2021-11-29 08:39
 **/


@Slf4j
@Component
public class TransDataFromRedis2DB {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionLikeMapper questionLikeMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Scheduled(fixedRate = 1000 * 60 * 30)
    void transLikeFromRedis2DB(){
        log.info("======= 点赞数据持久化定时任务开始，时间：{} ======", new Date());
        Long row1 = updateQuestionLikeFromRedis();
        Long row2 = updateQuestionLikeCountFromRedis();
        Long row3 = updateCommentLikeFromRedis();
        Long row4 = updateCommentLikeCountFromRedis();
        log.info("======= 点赞数据持久化定时任务结束，处理问题点赞详情数：{}, 问题点赞数：{}，评论点赞详情数{}, 评论点赞数{} ======", row1, row2, row3, row4);
    }


    @Transactional
    public Long updateQuestionLikeFromRedis() {
        Long counter = 0L;
        QuestionLike questionLike = new QuestionLike();
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_QUESTION_LIKE);
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            questionLike.setUserId(Integer.valueOf(strings[0]));
            questionLike.setQuestionId(Integer.valueOf(strings[1]));
            questionLike.setStatus((int)map.get(key));
            questionLike.setGmtCreate(new Date());
            questionLike.setGmtModified(new Date());
            counter += questionLikeMapper.insertOrUpdateLike(questionLike);
        }
        redisUtils.delete(RedisKeyEnum.MAP_QUESTION_LIKE);
        return counter;
    }


    @Transactional
    public Long updateQuestionLikeCountFromRedis() {
        Long counter = 0L;
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_QUESTION_LIKE);
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Question question = questionMapper.selectQuestionById(keyInteger);
            Integer likeCount = (Integer) map.get(key);
            question.setLikeCount(likeCount);
            question.setGmtModified(new Date());
            counter += questionMapper.updateLikeCount(question);
        }
        redisUtils.delete(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT);
        return counter;
    }

    @Transactional
    public Long updateCommentLikeFromRedis() {
        Long counter = 0L;
        CommentLike commentLike = new CommentLike();
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_COMMENT_LIKE);
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            commentLike.setUserId(Integer.valueOf(strings[0]));
            commentLike.setCommentId(Integer.valueOf(strings[1]));
            commentLike.setStatus((int)map.get(key));
            commentLike.setGmtCreate(new Date());
            commentLike.setGmtModified(new Date());
            commentLikeMapper.insertOrUpdateLike(commentLike);
        }
        redisUtils.delete(RedisKeyEnum.MAP_COMMENT_LIKE);
        return counter;
    }

    @Transactional
    public Long updateCommentLikeCountFromRedis(){
        Long counter = 0L;
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT);
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Comment comment = commentMapper.selectById(keyInteger);
            Integer likeCount = (Integer) map.get(key);
            comment.setLikeCount(likeCount);
            comment.setGmtModified(new Date());
            commentMapper.updateById(comment);
        }
        redisUtils.delete(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT);
        return counter;
    }
}
