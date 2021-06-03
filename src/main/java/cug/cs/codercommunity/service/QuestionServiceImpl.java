package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuestionServiceImpl implements QuestionService{
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public void creatQuestion(String title, String description, String tag, User user) {
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
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

    @Override
    public PageDto getOnePage(Integer page, Integer size) {
        Integer totalCount = questionMapper.selectCount();
        //开始为pageDto赋值
        PageDto pageDto = new PageDto();
        //计算总页数
        if (totalCount % size == 0){
            pageDto.setTotalPage(totalCount / size);
        } else {
            pageDto.setTotalPage(totalCount / size + 1);
        }
        //非法页校正
        if (page < 1) page = 1;
        if (page > pageDto.getTotalPage()) page = pageDto.getTotalPage();
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
        List<Question> questionList = questionMapper.selectOnePage(offset, size);
        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Question question : questionList) {
            QuestionVO questionVO = new QuestionVO();
            User user = userMapper.selectUserById(question.getCreator());
            BeanUtils.copyProperties(question, questionVO);
            questionVO.setUser(user);
            questionVOList.add(questionVO);
        }
        //放入question列表
        pageDto.setQuestionVOList(questionVOList);
        return pageDto;
    }
}
