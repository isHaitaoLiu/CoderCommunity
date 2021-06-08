package cug.cs.codercommunity.service;


import cug.cs.codercommunity.enums.CommentType;
import cug.cs.codercommunity.model.Comment;
import cug.cs.codercommunity.vo.CommentVO;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment);

    List<CommentVO> findAllCommentsByTargetId(Integer id, CommentType question);
}
