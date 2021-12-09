package cug.cs.codercommunity.service;

/**
 * @program: codercommunity
 * @description: 点赞服务接口
 * @author: Sakura
 * @create: 2021-11-28 18:41
 **/

public interface QuestionLikeService {
    boolean questionLike(Integer userId, Integer questionId, Integer status);

    Integer updateLikeFromRedis();

    boolean commentLike(Integer userId, Integer commentId, Integer status);

    Integer updateCommentLikeFromRedis();
}
