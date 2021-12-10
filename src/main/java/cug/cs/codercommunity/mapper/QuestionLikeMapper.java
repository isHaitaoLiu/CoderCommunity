package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.QuestionLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: codercommunity
 * @description: 点赞表持久化接口
 * @author: Sakura
 * @create: 2021-11-29 09:08
 **/

@Mapper
public interface QuestionLikeMapper extends BaseMapper<QuestionLike> {
    Integer insertOrUpdateLike(QuestionLike questionLike);

    QuestionLike selectByUserIdAndQuestionId(Integer userId, Integer questionId);

    Integer selectStatus(Integer userId, Integer questionId);
}
