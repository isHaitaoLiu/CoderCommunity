package cug.cs.codercommunity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @program: codercommunity
 * @description: 通知消息实体类
 * @author: Sakura
 * @create: 2021-12-07 15:50
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationMessage {
    //消息主题
    private String topic;
    //通知实体的id
    private Integer outerId;
    //消息发送者
    private Integer notifier;
    //消息接收者
    private Integer receiver;
    //其他数据
    private Map<String, Object> data;
}
