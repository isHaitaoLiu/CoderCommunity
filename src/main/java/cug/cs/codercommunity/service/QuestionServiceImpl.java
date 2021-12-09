package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.enums.KafkaNotificationTopicEnum;
import cug.cs.codercommunity.enums.LikeStatusEnum;
import cug.cs.codercommunity.exception.CustomException;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.QuestionLikeMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.message.notification.NotificationMessageProducer;
import cug.cs.codercommunity.model.*;
import cug.cs.codercommunity.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService{
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QuestionLikeMapper questionLikeMapper;
    @Autowired
    private NotificationMessageProducer notificationMessageProducer;


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
            QuestionVO questionVO = Question2QuestionVO(question, creator, user, redisTemplate);
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
        return Question2QuestionVO(question, creator, user, redisTemplate);
    }


    /**
     * @Author sakura
     * @Description question转questionVO
     * @Date 2021/11/28
     * @Param [question, creator, user, redisTemplate]
     * @return cug.cs.codercommunity.vo.QuestionVO
     **/
    @Override
    public QuestionVO Question2QuestionVO(Question question, User creator, User user, RedisTemplate<String, Object> redisTemplate){
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setUser(creator);
        //接下来设置点赞状态
        Object likeObj;
        if (user != null){
            //先查询缓存，有可能在缓存中更新过
            likeObj = redisTemplate.opsForHash().get("MAP_QUESTION_LIKE", user.getId() + ":" + question.getId());
            if (likeObj != null) {
                //缓存存在，直接设置
                questionVO.setLikeStatus((Integer) likeObj);
            }else {
                //缓存不存在，查询数据库
                QuestionLike questionLike;
                questionLike = questionLikeMapper.selectByUserIdAndQuestionId(user.getId(), question.getId());
                if (questionLike != null){
                    //数据库存在，设置状态
                    questionVO.setLikeStatus(questionLike.getStatus());
                }else {
                    //数据库不存在，设置不喜欢状态
                    questionVO.setLikeStatus(LikeStatusEnum.UNLIKE.getStatus());
                }
            }
        }else {
            //用户未登录，设置不喜欢状态
            questionVO.setLikeStatus(LikeStatusEnum.UNLIKE.getStatus());
        }

        //接下来获取点赞数, 点赞数有可能被更新过
        Object likeCount = redisTemplate.opsForHash().get("MAP_QUESTION_LIKE_COUNT", String.valueOf(question.getId()));
        if (likeCount == null){
            //将点赞信息存储在redis中
            redisTemplate.opsForHash().put("MAP_QUESTION_LIKE_COUNT", String.valueOf(question.getId()), question.getLikeCount());
        }else {
            //设置最新的点赞数
            questionVO.setLikeCount((Integer) likeCount);
        }
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

    @Override
    @Transactional
    public Integer updateLikeCountFromRedis() {
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries("MAP_QUESTION_LIKE_COUNT");
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Question question = questionMapper.selectQuestionById(keyInteger);
            Integer likeCount = (Integer)map.get(key);
            question.setLikeCount(likeCount);
            question.setGmtModified(new Date());
            counter += questionMapper.updateLikeCount(question);
            redisTemplate.opsForHash().delete("MAP_QUESTION_LIKE_COUNT", key);
        }
        return counter;
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
        String key = userId + ":" + questionId;
        if (status.equals(LikeStatusEnum.UNLIKE.getStatus())){
            //当前状态为未点赞，则进行点赞
            redisTemplate.opsForHash().put("MAP_QUESTION_LIKE", key, LikeStatusEnum.LIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_QUESTION_LIKE_COUNT", String.valueOf(questionId), 1L);
            //发送kafka消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTopic(KafkaNotificationTopicEnum.TOPIC_LIKE_QUESTION.getTopic());
            notificationMessage.setNotifier(userId);
            Question question = questionMapper.selectQuestionById(questionId);
            notificationMessage.setReceiver(question.getCreator());
            notificationMessage.setOuterId(questionId);
            notificationMessageProducer.sendMessage(notificationMessage);
        }else {
            redisTemplate.opsForHash().put("MAP_QUESTION_LIKE", key, LikeStatusEnum.UNLIKE.getStatus());
            redisTemplate.opsForHash().increment("MAP_QUESTION_LIKE_COUNT", String.valueOf(questionId), -1L);
        }
        return true;
    }


    @Override
    @Transactional
    public Integer updateQuestionLikeFromRedis() {
        QuestionLike questionLike = new QuestionLike();
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries("MAP_QUESTION_LIKE");
        for (Object key : map.keySet()) {
            String keyStr = (String) key;
            String[] strings = keyStr.split(":");
            questionLike.setUserId(Integer.valueOf(strings[0]));
            questionLike.setQuestionId(Integer.valueOf(strings[1]));
            questionLike.setStatus((int)map.get(key));
            questionLike.setGmtCreate(new Date());
            questionLike.setGmtModified(new Date());
            counter += questionLikeMapper.insertOrUpdateLike(questionLike);
            redisTemplate.opsForHash().delete("MAP_QUESTION_LIKE", key);
        }
        return counter;
    }
}
