package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.QuestionLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @program: codercommunity
 * @description: 点赞表持久化接口
 * @author: Sakura
 * @create: 2021-11-29 09:08
 **/

@Mapper
public interface QuestionLikeMapper extends BaseMapper<QuestionLike> {
    Integer insertOrUpdateLike(QuestionLike questionLike);

    @Select("select * from `question_like` where user_id = #{userId} and question_id = #{questionId}")
    QuestionLike selectByUserIdAndQuestionId(Integer userId, Integer questionId);

    @Select("select status from `question_like` where user_id = #{userId} and question_id = #{questionId}")
    Integer selectStatus(Integer userId, Integer questionId);
}
