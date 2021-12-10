package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question>{

    List<Question> selectOnePage(Integer offset, Integer size);

    List<Question> selectOnePageByCreator(Integer creator, Integer offset, Integer size);

    Integer incViewCount(Question question);

    Integer incCommentCount(Question question);

    Integer updateLikeCount(Question question);

    List<Question> selectRelated(Question question);

    Integer selectLikeCount(Integer questionId);

    List<Question> selectHotQuestions(Integer offset, Integer size);
}
