package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.dto.CommentDto;
import cug.cs.codercommunity.dto.JsonResult;
import cug.cs.codercommunity.enums.CommentType;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.model.Comment;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.CommentService;
import cug.cs.codercommunity.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public JsonResult<Void> postComment(@RequestBody CommentDto commentDto,
                                        HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null){
            return new JsonResult<>(CustomStatus.NOT_LOGIN);
        }

        if (commentDto == null || StringUtils.isAllBlank(commentDto.getContent())){
            return new JsonResult<>(CustomStatus.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentDto.getParentId());
        comment.setType(commentDto.getType());
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        //comment.setLikeCount(0);
        comment.setContent(commentDto.getContent());
        commentService.addComment(comment);
        return new JsonResult<>(CustomStatus.SUCCESS);
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public JsonResult<List<CommentVO>> getSubComments(@PathVariable(name = "id") Integer id){
        List<CommentVO> comments = commentService.findAllCommentsByTargetId(null, id, CommentType.COMMENT);
        return new JsonResult<>(CustomStatus.SUCCESS, comments);
    }
}
