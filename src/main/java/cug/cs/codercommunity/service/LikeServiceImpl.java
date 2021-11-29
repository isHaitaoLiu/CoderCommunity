package cug.cs.codercommunity.service;

import cug.cs.codercommunity.enums.LikeStatusEnum;
import cug.cs.codercommunity.mapper.LikeMapper;
import cug.cs.codercommunity.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    final String REDIS_MAP_LIKE = "REDIS_MAP_LIKE";

    final String REDIS_MAP_LIKE_COUNT = "REDIS_MAP_LIKE_COUNT";



    /*
     * @Author sakura
     * @Description 点赞，如果已经点赞，返回false
     * @Date 2021/11/28
     * @Param [key]
     * @return boolean
     **/
    @Override
    public boolean addLike(Integer userId, Integer questionId) {
        //log.info("点赞开始");
        String key = userId + ":" + questionId;
        Object obj = redisTemplate.opsForHash().get(REDIS_MAP_LIKE, key);
        if (obj != null && obj.equals(LikeStatusEnum.LIKE.getStatus())){
            return false;
        }else {
            redisTemplate.opsForHash().put(REDIS_MAP_LIKE, key, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment(REDIS_MAP_LIKE_COUNT, String.valueOf(questionId), 1L);
            return true;
        }
    }


    /*
     * @Author sakura
     * @Description 取消点赞，如果已经取消或未点赞，返回false
     * @Date 2021/11/28
     * @Param [key]
     * @return boolean
     **/
    @Override
    public boolean removeLike(Integer userId, Integer questionId) {
        //log.info("消赞开始");
        String key = userId + ":" + questionId;
        Object obj = redisTemplate.opsForHash().get(REDIS_MAP_LIKE, key);
        if (obj == null || obj.equals(LikeStatusEnum.UNLIKE.getStatus())){
            return false;
        }else {
            redisTemplate.opsForHash().put(REDIS_MAP_LIKE, key, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment(REDIS_MAP_LIKE_COUNT, String.valueOf(questionId), -1L);
            return true;
        }
    }

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
            return addLike(userId, questionId);
        }else {
            return removeLike(userId, questionId);
        }
    }


    @Override
    public Integer updateLikeFromRedis() {
        Like like = new Like();
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(REDIS_MAP_LIKE);
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            like.setUserId(Integer.valueOf(strings[0]));
            like.setQuestionId(Integer.valueOf(strings[1]));
            like.setStatus((int)map.get(key));
            like.setGmtCreate(System.currentTimeMillis());
            like.setGmtModified(System.currentTimeMillis());
            counter += likeMapper.insertOrUpdateLike(like);
        }
        return counter;
    }
}
