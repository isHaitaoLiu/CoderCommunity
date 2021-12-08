package cug.cs.codercommunity.enums;

/**
 * @program: codercommunity
 * @description: 消息主题枚举类
 * @author: Sakura
 * @create: 2021-12-07 16:16
 **/

public enum KafkaNotificationTopicEnum {
    TOPIC_REPLY_QUESTION(1, "replyQuestion"),
    TOPIC_REPLY_COMMENT(2, "replyComment"),
    TOPIC_LIKE_QUESTION(3, "likeQuestion"),
    TOPIC_LIKE_COMMENT(4, "likeComment");

    private final Integer type;
    private final String topic;

    KafkaNotificationTopicEnum(Integer type, String topic) {
        this.type = type;
        this.topic = topic;
    }

    public Integer getType() {
        return type;
    }

    public String getTopic() {
        return topic;
    }

    public static Integer getTypeByTopic(String topic){
        for (KafkaNotificationTopicEnum topicEnum : KafkaNotificationTopicEnum.values()) {
            if (topicEnum.topic.equals(topic)){
                return topicEnum.type;
            }
        }
        return -1;
    }
}
