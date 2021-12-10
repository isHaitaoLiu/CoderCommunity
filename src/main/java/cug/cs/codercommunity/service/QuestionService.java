package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.vo.QuestionVO;

import java.util.List;

public interface QuestionService {
    void creatQuestion(String title, String description, String tag, User user);

    PageDto<QuestionVO> getOnePage(Integer page, Integer size, User user);

    QuestionVO getQuestionById(Integer id, User user);

    void updateQuestion(String title, String description, String tag, User user, Integer questionId);

    void incView(Integer id);

    List<QuestionVO> getRelatedQuestions(QuestionVO questionVO);

    QuestionVO Question2QuestionVO(Question question, User creator, User user);

    boolean questionLike(Integer userId, Integer questionId, Integer status);
}
