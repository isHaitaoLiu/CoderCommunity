package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.service.UserService;
import cug.cs.codercommunity.utils.RedisUtils;
import cug.cs.codercommunity.vo.HotQuestionVO;
import cug.cs.codercommunity.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class IndexController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private QuestionService questionService;


    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size){
        PageDto<QuestionVO> pagination = questionService.getOnePage(page, size, null);
        model.addAttribute("pagination", pagination);
        //List<String> tags = hotTopicCache.getHots();
        //model.addAttribute("tags", tags);
        List<HotQuestionVO> hotQuestionVOList = redisUtils.getHotQuestionsFromRedis();
        model.addAttribute("hotQuestions", hotQuestionVOList);
        return "index";
    }
}
