package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.enums.KafkaNotificationTopicEnum;
import cug.cs.codercommunity.enums.LikeStatusEnum;
import cug.cs.codercommunity.enums.UpdateScoreTypeEnum;
import cug.cs.codercommunity.exception.CustomException;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.message.notification.NotificationMessageProducer;
import cug.cs.codercommunity.model.NotificationMessage;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.utils.RedisUtils;
import cug.cs.codercommunity.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService{
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMessageProducer notificationMessageProducer;
    @Autowired
    private RedisUtils redisUtils;


    /*
     * @Author sakura
     * @Description 创建问题
     * @Date 2021/11/28
     * @Param [title, description, tag, user]
     * @return void
     **/
    @Override
    public void creatQuestion(String title, String description, String tag, User user) {
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(new Date());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.insertQuestion(question);
    }

    @Override
    public List<QuestionVO> getAllQuestionVO() {
        List<Question> questionList = questionMapper.selectAllQuestion();
        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Question question : questionList) {
            QuestionVO questionVO = new QuestionVO();
            User user = userMapper.selectUserById(question.getCreator());
            BeanUtils.copyProperties(question, questionVO);
            questionVO.setUser(user);
            questionVOList.add(questionVO);
        }
        return questionVOList;
    }

    /**
     * @Author sakura
     * @Description 查询一页question
     * @Date 2021/11/28
     * @Param [page, size, user]
     * @return cug.cs.codercommunity.dto.PageDto<cug.cs.codercommunity.vo.QuestionVO>
     **/
    @Override
    public PageDto<QuestionVO> getOnePage(Integer page, Integer size, User user) {
        Integer totalCount;
        if (user == null){
            totalCount = questionMapper.selectCount();
        }else {
            totalCount = questionMapper.selectCountByCreator(user.getId());
        }
        //开始为pageDto赋值
        PageDto<QuestionVO> pageDto = new PageDto<>();
        //计算总页数
        if (totalCount % size == 0){
            pageDto.setTotalPage(totalCount / size);
        } else {
            pageDto.setTotalPage(totalCount / size + 1);
        }
        //非法页校正
        if (page > pageDto.getTotalPage()) page = pageDto.getTotalPage();
        if (page < 1) page = 1;
        //记录当前页
        pageDto.setCurrentPage(page);
        //计算当前分页状态栏记录的页码
        pageDto.setPages(new ArrayList<>());
        List<Integer> pages = pageDto.getPages();
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0){
                pages.add(0, page - i);
            }
        }
        for (int i = 1; i <= 3; i++) {
            if (page + i <= pageDto.getTotalPage()){
                pages.add(page + i);
            }
        }
        //是否展示上一页、下一页符号
        pageDto.setShowPrevious(page != 1);
        pageDto.setShowNext(!page.equals(pageDto.getTotalPage()));

        //是否展示第一页、最后一页符号
        pageDto.setShowFirstPage(!pages.contains(1));
        pageDto.setShowLastPage(!pages.contains(pageDto.getTotalPage()));
        Integer offset = (page - 1) * size;
        List<Question> questionList;
        if (user == null){
            questionList = questionMapper.selectOnePage(offset, size);
        }else {
            questionList = questionMapper.selectOnePageByCreator(user.getId(), offset, size);
        }
        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Question question : questionList) {
            User creator = userMapper.selectUserById(question.getCreator());
            QuestionVO questionVO = Question2QuestionVO(question, creator, user);
            questionVOList.add(questionVO);
        }
        //放入question列表
        pageDto.setData(questionVOList);
        return pageDto;
    }

    /**
     * @Author sakura
     * @Description 通过问题ID获取questionVO
     * @Date 2021/11/28
     * @Param [id, user]
     * @return cug.cs.codercommunity.vo.QuestionVO
     **/
    @Override
    public QuestionVO getQuestionById(Integer id, User user) {
        Question question = questionMapper.selectQuestionById(id);
        if (question == null){
            throw new CustomException(CustomStatus.QUESTION_NOT_FOUND);
        }
        User creator = userMapper.selectUserById(question.getCreator());
        return Question2QuestionVO(question, creator, user);
    }


    /**
     * @Author sakura
     * @Description question转questionVO
     * @Date 2021/11/28
     * @Param [question, creator, user]
     * @return cug.cs.codercommunity.vo.QuestionVO
     **/
    @Override
    public QuestionVO Question2QuestionVO(Question question, User creator, User user){
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setUser(creator);
        //设置点赞状态
        if (user != null){
            Integer likeStatus = redisUtils.getAndSetQuestionLikeStatus(user.getId(), question.getId());
            questionVO.setLikeStatus(likeStatus);
        }else {
            //用户未登录，设置不喜欢状态
            questionVO.setLikeStatus(LikeStatusEnum.UNLIKE.getStatus());
        }
        //设置点赞数
        Integer likeCount = redisUtils.getAndSetQuestionLikeCount(question.getId(), question.getLikeCount());
        questionVO.setLikeCount(likeCount);

        return questionVO;
    }

    @Override
    public void updateQuestion(String title, String description, String tag, User user, Integer questionId) {
        Question question = questionMapper.selectQuestionById(questionId);
        if (question == null){
            question = new Question();
            question.setCreator(user.getId());
            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setGmtCreate(new Date());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertQuestion(question);
        }else {
            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setGmtModified(new Date());
            if (questionMapper.updateQuestion(question) == 0){
                throw new CustomException(CustomStatus.QUESTION_NOT_FOUND);
            }
        }
    }

    @Override
    public void incView(Integer id) {
        Question question = questionMapper.selectQuestionById(id);
        questionMapper.incViewCount(question);
        //更新分数
        redisUtils.updateQuestionScoreByType(id, UpdateScoreTypeEnum.VIEW);
    }

    @Override
    public List<QuestionVO> getRelatedQuestions(QuestionVO questionVO) {
        if (questionVO.getTag() == null){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question questionDbSearch = new Question();
        questionDbSearch.setTag(regexpTag);
        questionDbSearch.setId(questionVO.getId());
        List<Question> relatedQuestions = questionMapper.selectRelated(questionDbSearch);

        List<QuestionVO> relatedQuestionVO = relatedQuestions.stream().map(question -> {
            QuestionVO qv = new QuestionVO();
            BeanUtils.copyProperties(question, qv);
            return qv;
        }).collect(Collectors.toList());
        return relatedQuestionVO;
    }



    /*
     * @Author sakura
     * @Description 点赞接口处理方法，检查传入的类型，如果是0，则进行点赞；如果是1，取消赞
     * @Date 2021/11/28
     * @Param [userId, questionId, type]
     * @return boolean
     **/
    @Override
    public boolean questionLike(Integer userId, Integer questionId, Integer status) {
        //更新问题点赞状态
        redisUtils.updateQuestionLikeStatus(userId, questionId, LikeStatusEnum.enumOfStatus(status));
        //点赞，发送消息
        if (status.equals(LikeStatusEnum.UNLIKE.getStatus())){
            //发送kafka消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTopic(KafkaNotificationTopicEnum.TOPIC_LIKE_QUESTION.getTopic());
            notificationMessage.setNotifier(userId);
            Question question = questionMapper.selectQuestionById(questionId);
            notificationMessage.setReceiver(question.getCreator());
            notificationMessage.setOuterId(questionId);
            notificationMessageProducer.sendMessage(notificationMessage);
        }
        return true;
    }
}
