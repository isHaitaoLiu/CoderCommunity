package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.JsonResult;
import cug.cs.codercommunity.enums.CommentType;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.CommentService;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.vo.CommentVO;
import cug.cs.codercommunity.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model,
                           HttpSession session){
        User user = (User) session.getAttribute("user");
        QuestionVO questionVO = questionService.getQuestionById(id, user);
        List<QuestionVO> relatedQuestions = questionService.getRelatedQuestions(questionVO);
        List<CommentVO> commentVOList = commentService.findAllCommentsByTargetId(user, id, CommentType.QUESTION);

        model.addAttribute("question", questionVO);
        //log.info("问题{}的点赞状态是：{}", questionVO.getId(), questionVO.getLikeStatus());
        model.addAttribute("comments", commentVOList);
        model.addAttribute("relatedQuestions", relatedQuestions);
        questionService.incView(id);
        return "question";
    }

    @ResponseBody
    @PostMapping("/question/like")
    public JsonResult<Object> questionLike(@RequestBody Map<String, String> map,
                                           HttpSession session){
        User user = (User)session.getAttribute("user");
        if (user == null){
            return new JsonResult<>(CustomStatus.NOT_LOGIN);
        }
        boolean isSuccess = questionService.questionLike(
                user.getId(),
                Integer.valueOf(map.get("questionId")),
                Integer.valueOf(map.get("status"))
        );
        return new JsonResult<>(CustomStatus.SUCCESS, isSuccess);
    }
}
