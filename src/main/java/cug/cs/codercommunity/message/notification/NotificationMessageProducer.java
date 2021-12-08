package cug.cs.codercommunity.message.notification;

import com.alibaba.fastjson.JSONObject;
import cug.cs.codercommunity.model.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;

/**
 * @program: codercommunity
 * @description: 通知消息生产者
 * @author: Sakura
 * @create: 2021-12-07 15:44
 **/

@Slf4j
@Component
public class NotificationMessageProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /*
     * @Author sakura
     * @Description 发送消息完成的回调函数
     * @Date 2021/12/7
     * @Param
     * @return
     **/
    public static class sendCallBack implements ListenableFutureCallback<SendResult<String, Object>>{
        @Override
        public void onFailure(Throwable ex) {
            log.error("{}, {}", ex.getMessage(), new Date());
        }

        @Override
        public void onSuccess(SendResult<String, Object> result) {
            if (result == null){
                log.info("发送空消息，{}", new Date());
            }else {
                log.info("发送{}消息成功 - {}", result.getRecordMetadata().topic(), new Date());
            }
        }
    }

    public void sendMessage(NotificationMessage message){
        //send方法默认是异步发送的
        kafkaTemplate.send(message.getTopic(), JSONObject.toJSONString(message)).addCallback(new sendCallBack());
    }
}
