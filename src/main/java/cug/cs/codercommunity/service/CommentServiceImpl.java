package cug.cs.codercommunity.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cug.cs.codercommunity.enums.*;
import cug.cs.codercommunity.exception.CustomExceptionToJson;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.*;
import cug.cs.codercommunity.message.notification.NotificationMessageProducer;
import cug.cs.codercommunity.model.*;
import cug.cs.codercommunity.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    @Autowired
    private NotificationMessageProducer notificationMessageProducer;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

            //数据库查询回复的是哪条评论
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null){
                throw new CustomExceptionToJson(CustomStatus.COMMENT_NOT_FOUND);
            }
            //查询这条评论属于哪个问题
            Question question = questionMapper.selectQuestionById(dbComment.getParentId());
            if (question == null){
                throw new CustomExceptionToJson(CustomStatus.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            //进行通知
            //createNotification(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT, question);
            //发送消息到kafka
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setNotifier(comment.getCommentator());
            notificationMessage.setReceiver(dbComment.getCommentator());
            notificationMessage.setOuterId(question.getId());
            notificationMessage.setTopic(KafkaNotificationTopicEnum.TOPIC_REPLY_COMMENT.getTopic());
            notificationMessageProducer.sendMessage(notificationMessage);
        }else {
            //回复问题
            Question question = questionMapper.selectQuestionById(comment.getParentId());
            if (question == null){
                throw new CustomExceptionToJson(CustomStatus.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionMapper.incCommentCount(question);
            //发送消息到kafka
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setNotifier(comment.getCommentator());
            notificationMessage.setReceiver(question.getCreator());
            notificationMessage.setOuterId(question.getId());
            notificationMessage.setTopic(KafkaNotificationTopicEnum.TOPIC_REPLY_QUESTION.getTopic());
            notificationMessageProducer.sendMessage(notificationMessage);
            //createNotification(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION, question);
        }
    }

    @Override
    public void createNotification(Comment comment, Integer receiver, NotificationTypeEnum typeEnum, Question question){
        Notification notification = new Notification();
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setOuterId(question.getId());
        notification.setType(typeEnum.getType());
        notification.setGmtCreate(new Date());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationMapper.insert(notification);
    }

    @Override
    public List<CommentVO> findAllCommentsByTargetId(User user, Integer id, CommentType commentType) {
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
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        //获取评论，用户绑定的对象列表
        List<CommentVO> commentVOList = comments.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setUser(userMap.get(comment.getCommentator()));

            //缓存处理开始

            //查询当前用户是否点赞
            if (user == null){
                //如果用户不存在，直接设置未点赞
                commentVO.setLikeStatus(LikeStatusEnum.UNLIKE.getStatus());
            }else {
                //当前用户已经登录，查找redis，是否更新过
                Object redisLike = redisTemplate.opsForHash().get("MAP_COMMENT_LIKE", user.getId() + ":" + commentVO.getId());
                if (redisLike != null){
                    //设置为缓存中的状态
                    commentVO.setLikeStatus((Integer)redisLike);
                }else {
                    //查询数据库，设置
                    CommentLike dbLike = commentLikeMapper.selectByUserIdAndCommentId(user.getId(), commentVO.getId());
                    if (dbLike != null){
                        commentVO.setLikeStatus(dbLike.getStatus());
                    }else {
                        commentVO.setLikeStatus(LikeStatusEnum.UNLIKE.getStatus());
                    }
                }
            }
            //设置redis总数
            Object redis_count = redisTemplate.opsForHash().get("MAP_COMMENT_LIKE_COUNT", String.valueOf(commentVO.getId()));
            if (redis_count == null){
                redisTemplate.opsForHash().put("MAP_COMMENT_LIKE_COUNT", String.valueOf(commentVO.getId()), commentVO.getLikeCount());
            }else {
                commentVO.setLikeCount((Integer)redis_count);
            }


            return commentVO;
        }).collect(Collectors.toList());

        return commentVOList;
    }

    @Override
    public Integer updateCommentLikeCountFromRedis(){
        Integer counter = 0;
        Map<Object, Object> map = redisTemplate.opsForHash().entries("MAP_COMMENT_LIKE_COUNT");
        for (Object key : map.keySet()) {
            Integer keyInteger = Integer.valueOf((String) key);
            Comment comment = commentMapper.selectById(keyInteger);
            Integer likeCount = (Integer)map.get(key);
            comment.setLikeCount(likeCount);
            comment.setGmtModified(new Date());
            counter += commentMapper.updateById(comment);
            redisTemplate.opsForHash().delete("MAP_COMMENT_LIKE_COUNT", key);
        }
        return counter;
    }
}