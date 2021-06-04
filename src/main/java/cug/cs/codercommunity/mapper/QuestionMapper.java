package cug.cs.codercommunity.mapper;

import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionMapper {
    public void insertQuestion(Question question);

    List<Question> selectAllQuestion();

    @Select("select * from question limit #{offset}, #{size}")
    List<Question> selectOnePage(Integer offset, Integer size);

    @Select("select * from question where creator = #{creator} limit #{offset}, #{size}")
    List<Question> selectOnePageByCreator(Integer creator, Integer offset, Integer size);

    @Select("select count(1) from question")
    Integer selectCount();

    @Select("select count(1) from question where creator = #{creator}")
    Integer selectCountByCreator(Integer creator);

    @Select("select * from question where id = #{id}")
    Question selectQuestionById(Integer id);

    int updateQuestion(Question question);
}
