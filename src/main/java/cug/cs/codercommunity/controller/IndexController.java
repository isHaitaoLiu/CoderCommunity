package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.cache.HotTopicCache;
import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTopicCache hotTopicCache;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size){
        PageDto pagination = questionService.getOnePage(page, size, null);
        model.addAttribute("pagination", pagination);
        List<String> tags = hotTopicCache.getHots();
        model.addAttribute("tags", tags);
        return "index";
    }
}
