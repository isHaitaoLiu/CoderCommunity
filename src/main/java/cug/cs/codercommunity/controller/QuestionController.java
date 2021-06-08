package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.enums.CommentType;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.service.CommentService;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.vo.CommentVO;
import cug.cs.codercommunity.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionVO question = questionService.getQuestionById(id);
        List<QuestionVO> relatedQuestions = questionService.getRelatedQuestions(question);
        List<CommentVO> commentVOList = commentService.findAllCommentsByTargetId(id, CommentType.QUESTION);
        model.addAttribute("question", question);
        model.addAttribute("comments", commentVOList);
        model.addAttribute("relatedQuestions", relatedQuestions);
        questionService.incView(id);
        return "question";
    }
}
