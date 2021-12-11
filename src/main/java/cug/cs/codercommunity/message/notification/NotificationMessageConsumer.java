package cug.cs.codercommunity.message.notification;

import com.alibaba.fastjson.JSONObject;
import cug.cs.codercommunity.enums.KafkaNotificationTopicEnum;
import cug.cs.codercommunity.enums.NotificationStatusEnum;
import cug.cs.codercommunity.mapper.CommentMapper;
import cug.cs.codercommunity.mapper.NotificationMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.model.Comment;
import cug.cs.codercommunity.model.Notification;
import cug.cs.codercommunity.model.NotificationMessage;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.constant.KafkaTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @program: codercommunity
 * @description: 通知消息消费者
 * @author: Sakura
 * @create: 2021-12-07 15:57
 **/

@Slf4j
@Component
public class NotificationMessageConsumer implements KafkaTopicConstant{
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @KafkaListener(groupId = "sakura-consumer", topics = {TOPIC_REPLY_COMMENT, TOPIC_LIKE_COMMENT, TOPIC_REPLY_QUESTION, TOPIC_LIKE_QUESTION})
    public void handleNotificationMessage(ConsumerRecord<String, Object> record){
        if (record == null || record.value() == null){
            log.error("=== 消息内容为空 ===");
            return;
        }
        NotificationMessage notificationMessage = JSONObject.parseObject(record.value().toString(), NotificationMessage.class);
        if (notificationMessage == null){
            log.error("=== 消息格式错误 ===");
            return;
        }
        Notification notification = new Notification();
        Integer type = KafkaNotificationTopicEnum.getTypeByTopic(record.topic());
        if (type == -1){
            log.error("=== 消息主题错误，当前消息主题:{} ===", record.topic());
            return;
        }
        notification.setType(type);
        notification.setOuterId(notificationMessage.getOuterId());
        notification.setReceiver(notificationMessage.getReceiver());
        notification.setNotifier(notificationMessage.getNotifier());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setGmtCreate(new Date());

        if (KafkaNotificationTopicEnum.TOPIC_LIKE_COMMENT.getTopic().equals(record.topic())){
            //如果是评论点赞主题，需要特殊处理
            Map<String, Object> data = notificationMessage.getData();
            Integer commentId = (Integer) data.get("commentId");
            Comment comment = commentMapper.selectById(commentId);
            notification.setReceiver(comment.getCommentator());
            Question question = questionMapper.selectById(comment.getParentId());
            notification.setOuterId(question.getId());
        }


        if (KafkaNotificationTopicEnum.TOPIC_LIKE_QUESTION.getTopic().equals(record.topic())){
            //如果是问题点赞主题，需要特殊处理
            Map<String, Object> data = notificationMessage.getData();
            Integer questionId = (Integer) data.get("questionId");
            Question question = questionMapper.selectById(questionId);
            notification.setReceiver(question.getCreator());
        }


        notificationMapper.insert(notification);
        log.info("=== 消息消费成功:{} ===", notificationMessage);
    }

}
