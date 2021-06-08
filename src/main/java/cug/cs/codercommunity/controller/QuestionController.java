package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionVO question = questionService.getQuestionById(id);
        model.addAttribute("question", question);
        questionService.incView(id);
        return "question";
    }
}
