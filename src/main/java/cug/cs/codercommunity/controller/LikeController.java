package cug.cs.codercommunity.controller;

import cug.cs.codercommunity.dto.JsonResult;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @program: codercommunity
 * @description: 点赞接口
 * @author: Sakura
 * @create: 2021-11-28 19:15
 **/

@Slf4j
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @ResponseBody
    @PostMapping("/like")
    public JsonResult<Object> likeOrUnlike(@RequestBody Map<String, String> map,
                                           HttpSession session){
        User user = (User)session.getAttribute("user");
        if (user == null){
            return new JsonResult<>(CustomStatus.NOT_LOGIN);
        }
        boolean isSuccess = likeService.handleLike(
                user.getId(),
                Integer.valueOf(map.get("questionId")),
                Integer.valueOf(map.get("status"))
        );
        //log.info("点赞原状态：{}，点赞是否成功：{}", map.get("status"), isSuccess);
        return new JsonResult<>(CustomStatus.SUCCESS, isSuccess);
    }
}
