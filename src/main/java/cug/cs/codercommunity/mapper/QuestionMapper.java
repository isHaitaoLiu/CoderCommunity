package cug.cs.codercommunity.mapper;

import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {
    public void insertQuestion(Question question);
}
