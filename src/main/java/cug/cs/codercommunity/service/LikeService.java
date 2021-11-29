package cug.cs.codercommunity.service;

/**
 * @program: codercommunity
 * @description: 点赞服务接口
 * @author: Sakura
 * @create: 2021-11-28 18:41
 **/

public interface LikeService {
    boolean addLike(Integer userId, Integer questionId);

    boolean removeLike(Integer userId, Integer questionId);

    boolean handleLike(Integer userId, Integer questionId, Integer status);

    Integer updateLikeFromRedis();
}
