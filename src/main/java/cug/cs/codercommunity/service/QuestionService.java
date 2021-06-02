package cug.cs.codercommunity.service;

import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.vo.QuestionVO;

import java.util.List;

public interface QuestionService {
    public void creatQuestion(String title, String description, String tag, User user);

    List<QuestionVO> getAllQuestionVO();
}
