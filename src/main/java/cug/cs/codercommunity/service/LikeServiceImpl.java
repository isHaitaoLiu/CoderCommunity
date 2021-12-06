package cug.cs.codercommunity.service;

import cug.cs.codercommunity.enums.LikeStatusEnum;
import cug.cs.codercommunity.mapper.CommentLikeMapper;
import cug.cs.codercommunity.mapper.LikeMapper;
import cug.cs.codercommunity.model.CommentLike;
import cug.cs.codercommunity.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @program: codercommunity
 * @description: 点赞服务类
 * @author: Sakura
 * @create: 2021-11-28 18:44
 **/

@Slf4j
@Service
public class LikeServiceImpl implements LikeService{
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CommentLikeMapper commentLikeMapper;


    /*
     * @Author sakura
     * @Description 点赞接口处理方法，检查传入的类型，如果是0，则进行点赞；如果是1，取消赞
     * @Date 2021/11/28
     * @Param [userId, questionId, type]
     * @return boolean
     **/
    @Override
    public boolean handleLike(Integer userId, Integer questionId, Integer status) {
        String key = userId + ":" + questionId;
        if (status.equals(LikeStatusEnum.UNLIKE.getStatus())){
            redisTemplate.opsForHash().put("MAP_QUESTION_LIKE", key, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_QUESTION_LIKE_COUNT", String.valueOf(questionId), 1L);
        }else {
            redisTemplate.opsForHash().put("MAP_QUESTION_LIKE", key, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_QUESTION_LIKE_COUNT", String.valueOf(questionId), -1L);
        }
        return true;
    }

    @Override
    public boolean commentLike(Integer userId, Integer commentId, Integer status) {
        String key = userId + ":" + commentId;
        if (status.equals(LikeStatusEnum.UNLIKE.getStatus())){
            redisTemplate.opsForHash().put("MAP_COMMENT_LIKE", key, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_COMMENT_LIKE_COUNT", String.valueOf(commentId), 1L);
        }else {
            redisTemplate.opsForHash().put("MAP_COMMENT_LIKE", key, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_COMMENT_LIKE_COUNT", String.valueOf(commentId), -1L);
        }
        return true;
    }


    @Override
    @Transactional
    public Integer updateLikeFromRedis() {
        Like like = new Like();
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries("MAP_QUESTION_LIKE");
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            like.setUserId(Integer.valueOf(strings[0]));
            like.setQuestionId(Integer.valueOf(strings[1]));
            like.setStatus((int)map.get(key));
            like.setGmtCreate(System.currentTimeMillis());
            like.setGmtModified(System.currentTimeMillis());
            counter += likeMapper.insertOrUpdateLike(like);
            redisTemplate.opsForHash().delete("MAP_QUESTION_LIKE", key);
        }
        return counter;
    }


    @Override
    @Transactional
    public Integer updateCommentLikeFromRedis() {
        CommentLike commentLike = new CommentLike();
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries("MAP_COMMENT_LIKE");
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            commentLike.setUserId(Integer.valueOf(strings[0]));
            commentLike.setCommentId(Integer.valueOf(strings[1]));
            commentLike.setStatus((int)map.get(key));
            commentLike.setGmtCreate(System.currentTimeMillis());
            commentLike.setGmtModified(System.currentTimeMillis());
            counter += commentLikeMapper.insertOrUpdateLike(commentLike);
            redisTemplate.opsForHash().delete("MAP_COMMENT_LIKE", key);
        }
        return counter;
    }

}
