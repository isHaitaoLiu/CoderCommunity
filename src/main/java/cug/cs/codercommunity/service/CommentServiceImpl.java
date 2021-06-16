package cug.cs.codercommunity.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cug.cs.codercommunity.enums.CommentType;
import cug.cs.codercommunity.enums.NotificationStatusEnum;
import cug.cs.codercommunity.enums.NotificationTypeEnum;
import cug.cs.codercommunity.exception.CustomExceptionToJson;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.CommentMapper;
import cug.cs.codercommunity.mapper.NotificationMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.model.Comment;
import cug.cs.codercommunity.model.Notification;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    @Override
    public void addComment(Comment comment) {
        if (comment.getParentId() == null){
            throw new CustomExceptionToJson(CustomStatus.TARGET_PARAM_WRONG);
        }

        if (comment.getType() == null || !CommentType.isExist(comment.getType())) {
            throw new CustomExceptionToJson(CustomStatus.TYPE_PARAM_WRONG);
        }

        CommentType c;
        if (comment.getType() == 2){
            c = CommentType.COMMENT;
        }else{
            c = CommentType.QUESTION;
        }

        if (c == CommentType.COMMENT){
            //回复评论
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null){
                throw new CustomExceptionToJson(CustomStatus.COMMENT_NOT_FOUND);
            }
            //查询问题，以便放入通知中
            Question question = questionMapper.selectQuestionById(dbComment.getParentId());
            if (question == null){
                throw new CustomExceptionToJson(CustomStatus.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            //进行通知
            createNotification(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT, question);
        }else {
            //回复问题
            Question question = questionMapper.selectQuestionById(comment.getParentId());
            if (question == null){
                throw new CustomExceptionToJson(CustomStatus.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionMapper.incCommentCount(question);
            //进行通知
            createNotification(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION, question);
        }
    }

    @Override
    public void createNotification(Comment comment, Integer receiver, NotificationTypeEnum typeEnum, Question question){
        Notification notification = new Notification();
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setOuterId(question.getId());
        notification.setType(typeEnum.getType());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationMapper.insert(notification);
    }

    @Override
    public List<CommentVO> findAllCommentsByTargetId(Integer id, CommentType commentType) {
        //查找该问题下的所有评论
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper
                .eq("parent_id", id)
                .eq("type", commentType.getType())
                .orderByDesc("gmt_create");

        List<Comment> comments = commentMapper.selectList(commentQueryWrapper);
        if (comments.size() == 0){
            return new ArrayList<>();
        }
        //获取评论人id列表
        Set<Integer> commentators = comments.stream().map(Comment::getCommentator).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList<>(commentators);

        //获取评论人{id, 评论人}map
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIds);
        List<User> users = userMapper.selectList(userQueryWrapper);
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        //获取评论，用户绑定的对象列表
        List<CommentVO> commentVOList = comments.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setUser(userMap.get(comment.getCommentator()));
            return commentVO;
        }).collect(Collectors.toList());

        return commentVOList;
    }
}
