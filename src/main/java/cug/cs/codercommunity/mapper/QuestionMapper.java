package cug.cs.codercommunity.mapper;

import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {
    public void insertQuestion(Question question);

    List<Question> selectAllQuestion();
}
