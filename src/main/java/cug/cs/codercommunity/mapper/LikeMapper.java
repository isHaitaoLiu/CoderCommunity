package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @program: codercommunity
 * @description: 点赞表持久化接口
 * @author: Sakura
 * @create: 2021-11-29 09:08
 **/

@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    Integer insertOrUpdateLike(Like like);

    @Select("select * from `like` where user_id = #{userId} and question_id = #{questionId}")
    Like selectByUserIdAndQuestionId(Integer userId, Integer questionId);
}
