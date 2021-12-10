package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.vo.HotQuestionVO;
import cug.cs.codercommunity.vo.QuestionVO;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface QuestionService {
    void creatQuestion(String title, String description, String tag, User user);

    List<QuestionVO> getAllQuestionVO();

    PageDto<QuestionVO> getOnePage(Integer page, Integer size, User user);

    QuestionVO getQuestionById(Integer id, User user);

    void updateQuestion(String title, String description, String tag, User user, Integer questionId);

    void incView(Integer id);

    List<QuestionVO> getRelatedQuestions(QuestionVO questionVO);

    QuestionVO Question2QuestionVO(Question question, User creator, User user, RedisTemplate<String, Object> redisTemplate);

    Integer updateQuestionLikeCountFromRedis();

    boolean questionLike(Integer userId, Integer questionId, Integer status);

    Integer updateQuestionLikeFromRedis();

    List<HotQuestionVO> getHotQuestionsFromRedis();
}
