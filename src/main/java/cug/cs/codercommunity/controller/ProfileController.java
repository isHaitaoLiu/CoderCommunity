package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class ProfileController {
    @Autowired
    QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile( HttpSession session,
                          Model model,
                          @PathVariable(name = "action") String action,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size){
        User user = (User)session.getAttribute("user");
        if (user == null){
            return "redirect:/";
        }
        if ("questions".equals(action)){
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
        }else if("replies".equals(action)){
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }
        final PageDto pagination = questionService.getOnePage(page, size, user);
        model.addAttribute("pagination", pagination);
        return "profile";
    }
}
