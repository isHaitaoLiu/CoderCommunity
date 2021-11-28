package cug.cs.codercommunity.controller;

import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.QuestionService;
import cug.cs.codercommunity.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                       Model model,
                       HttpSession session){
        QuestionVO questionVO = questionService.getQuestionById(id);
        model.addAttribute("title", questionVO.getTitle());
        model.addAttribute("description", questionVO.getDescription());
        model.addAttribute("tag", questionVO.getTag());
        session.setAttribute("questionId", id);
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag,
            HttpSession session,
            Model model){
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);

        if (title == null || "".equals(title)){
            model.addAttribute("error", "标题不能为空！");
            return "publish";
        }
        if (description == null || "".equals(description)){
            model.addAttribute("error", "问题补充不能为空！");
            return "publish";
        }
        if (tag == null || "".equals(tag)){
            model.addAttribute("error", "标签不能为空！");
            return "publish";
        }

        User user = (User)session.getAttribute("user");
        if (user == null){
            model.addAttribute("error", "用户未登录");
            return "publish";
        }
        Integer questionId = (Integer) session.getAttribute("questionId");
        if (questionId == null){
            questionService.creatQuestion(title, description, tag, user);
        }else {
            questionService.updateQuestion(title, description, tag, user, questionId);
            session.removeAttribute("questionId");
        }
        return "redirect:/";
    }
}
