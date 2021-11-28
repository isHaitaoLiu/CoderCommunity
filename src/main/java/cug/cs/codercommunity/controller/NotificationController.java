package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.NotificationDto;
import cug.cs.codercommunity.enums.NotificationTypeEnum;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpSession session,
                          @PathVariable(name = "id") Integer id){
        User user = (User) session.getAttribute("user");
        if (user == null){
            return "redirect:/";
        }
        NotificationDto notificationDto = notificationService.read(id, user);
        if (NotificationTypeEnum.REPLY_COMMENT.getType().equals(notificationDto.getType())
        || NotificationTypeEnum.REPLY_QUESTION.getType().equals(notificationDto.getType())){
            return "redirect:/question/" + notificationDto.getOuterId();
        }else {
            return "redirect:/";
        }
    }
}
