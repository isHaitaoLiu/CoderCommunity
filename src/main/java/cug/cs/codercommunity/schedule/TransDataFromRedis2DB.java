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
        Long row5 = updateQuestionViewCountFromRedis();
        log.info("======= 点赞数据持久化定时任务结束，处理问题点赞详情数：{}, 问题点赞数：{}，评论点赞详情数{}, 评论点赞数{}, 问题浏览数{} ======", row1, row2, row3, row4, row5);
    }


    @Transactional
    Long updateQuestionLikeFromRedis() {
        Long counter = 0L;
        QuestionLike questionLike = new QuestionLike();
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_QUESTION_LIKE);
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            int likeStatus = (int)map.get(key);
            String[] strings = keyStr.split(":");
            questionLike.setUserId(Integer.valueOf(strings[0]));
            questionLike.setQuestionId(Integer.valueOf(strings[1]));
            questionLike.setStatus(likeStatus);
            questionLike.setGmtCreate(new Date());
            questionLike.setGmtModified(new Date());
            counter += questionLikeMapper.insertOrUpdateLike(questionLike);
            redisUtils.deleteNoUpdatedHashKeyByLua(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), keyStr, likeStatus);
            //RedisScript<Object> redisScript = RedisScript.of(new ClassPathResource("src/main/resources/lua"));
            //redisTemplate.execute(redisScript, Collections.singletonList(RedisKeyEnum.MAP_QUESTION_LIKE.getKey()), key, map.get(key));
        }
        //redisUtils.delete(RedisKeyEnum.MAP_QUESTION_LIKE);
        return counter;
    }


    @Transactional
    Long updateQuestionLikeCountFromRedis() {
        Long counter = 0L;
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_QUESTION_LIKE);
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            int likeCount = (Integer) map.get(key);
            Question question = questionMapper.selectById(keyInteger);
            question.setLikeCount(likeCount);
            question.setGmtModified(new Date());
            counter += questionMapper.updateLikeCount(question);
            redisUtils.deleteNoUpdatedHashKeyByLua(RedisKeyEnum.MAP_QUESTION_LIKE.getKey(), (String) key, likeCount);

        }
        //redisUtils.delete(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT);
        return counter;
    }

    @Transactional
    Long updateCommentLikeFromRedis() {
        Long counter = 0L;
        CommentLike commentLike = new CommentLike();
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_COMMENT_LIKE);
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            int likeStatus = (int)map.get(key);
            String[] strings = keyStr.split(":");
            commentLike.setUserId(Integer.valueOf(strings[0]));
            commentLike.setCommentId(Integer.valueOf(strings[1]));
            commentLike.setStatus(likeStatus);
            commentLike.setGmtCreate(new Date());
            commentLike.setGmtModified(new Date());
            counter += commentLikeMapper.insertOrUpdateLike(commentLike);
            redisUtils.deleteNoUpdatedHashKeyByLua(RedisKeyEnum.MAP_COMMENT_LIKE.getKey(), keyStr, likeStatus);
        }
        //redisUtils.delete(RedisKeyEnum.MAP_COMMENT_LIKE);
        return counter;
    }

    @Transactional
    Long updateCommentLikeCountFromRedis(){
        Long counter = 0L;
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT);
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Integer likeCount = (Integer) map.get(key);
            Comment comment = commentMapper.selectById(keyInteger);
            comment.setLikeCount(likeCount);
            comment.setGmtModified(new Date());
            counter += commentMapper.updateById(comment);
            redisUtils.deleteNoUpdatedHashKeyByLua(RedisKeyEnum.MAP_COMMENT_LIKE_COUNT.getKey(), (String) key, likeCount);

        }
        return counter;
    }

    @Transactional
    Long updateQuestionViewCountFromRedis(){
        Long counter = 0L;
        Map<Object, Object> map = redisUtils.getAllKeyValues(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT);
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Integer viewCount = (Integer) map.get(key);
            Question question = questionMapper.selectById(keyInteger);
            question.setViewCount(viewCount);
            question.setGmtModified(new Date());
            counter += questionMapper.updateById(question);
            redisUtils.deleteNoUpdatedHashKeyByLua(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT.getKey(), (String) key, viewCount);
        }
        //redisUtils.delete(RedisKeyEnum.MAP_QUESTION_VIEW_COUNT);
        return counter;
    }
}
