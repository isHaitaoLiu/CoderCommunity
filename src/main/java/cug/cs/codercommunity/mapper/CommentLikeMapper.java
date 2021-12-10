package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: codercommunity
 * @description: 评论点赞详情的查询接口
 * @author: Sakura
 * @create: 2021-12-05 21:18
 **/

@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
    Integer insertOrUpdateLike(CommentLike commentLike);

    CommentLike selectByUserIdAndCommentId(Integer userId, Integer commentId);

    Integer selectStatus(Integer userId, Integer commentId);
}
