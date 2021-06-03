package cug.cs.codercommunity.mapper;

import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    public void insertQuestion(Question question);

    List<Question> selectAllQuestion();

    @Select("select * from question limit #{offset}, #{size}")
    List<Question> selectOnePage(Integer offset, Integer size);

    @Select("select count(1) from question")
    Integer selectCount();
}
