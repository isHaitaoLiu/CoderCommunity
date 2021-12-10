package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Override
    Comment selectById(Serializable id);

    int insertComment(Comment comment);

    @Select("select like_count from comment where id = #{commentId}")
    Integer selectLikeCount(Integer commentId);
}
