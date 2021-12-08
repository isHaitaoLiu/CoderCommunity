package cug.cs.codercommunity.message.notification;

import com.alibaba.fastjson.JSONObject;
import cug.cs.codercommunity.enums.KafkaNotificationTopicEnum;
import cug.cs.codercommunity.enums.NotificationStatusEnum;
import cug.cs.codercommunity.mapper.NotificationMapper;
import cug.cs.codercommunity.model.Notification;
import cug.cs.codercommunity.model.NotificationMessage;
import cug.cs.codercommunity.model.constant.KafkaTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(groupId = "sakura-consumer", topics = {TOPIC_REPLY_COMMENT, TOPIC_LIKE_COMMENT, TOPIC_REPLY_QUESTION, TOPIC_LIKE_QUESTION})
    public void handleNotificationMessage(ConsumerRecord<String, Object> record){
        if (record == null || record.value() == null){
            log.error("消息内容为空");
            return;
        }
        NotificationMessage notificationMessage = JSONObject.parseObject(record.value().toString(), NotificationMessage.class);
        if (notificationMessage == null){
            log.error("消息格式错误");
            return;
        }
        Notification notification = new Notification();
        Integer type = KafkaNotificationTopicEnum.getTypeByTopic(record.topic());
        if (type == -1){
            log.error("消息主题错误，当前消息主题:{}", record.topic());
            return;
        }
        notification.setType(type);
        notification.setOuterId(notificationMessage.getOuterId());
        notification.setReceiver(notificationMessage.getReceiver());
        notification.setNotifier(notificationMessage.getNotifier());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setGmtCreate(System.currentTimeMillis());
        notificationMapper.insert(notification);
        log.info("消息消费成功:{}", notificationMessage);
    }

}
