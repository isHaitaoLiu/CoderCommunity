package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.service.UserService;
import cug.cs.codercommunity.vo.QuestionVO;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String indexController(@CookieValue(name = "token", required = false) Cookie token,
                                  HttpSession session,
                                  Model model,
                                  @RequestParam(name = "page", defaultValue = "1") Integer page,
                                  @RequestParam(name = "size", defaultValue = "5") Integer size){
        if (token != null){
            User user = userService.findUserByToken(token.getValue());
            if (user != null) session.setAttribute("user", user);
        }
        //List<QuestionVO> questionVOList = questionService.getAllQuestionVO();
        PageDto pagination = questionService.getOnePage(page, size);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
