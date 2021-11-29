package cug.cs.codercommunity.schedule;

import cug.cs.codercommunity.service.LikeService;
import cug.cs.codercommunity.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: codercommunity
 * @description: 将点赞详情从redis中保存到数据库
 * @author: Sakura
 * @create: 2021-11-29 08:39
 **/


@Slf4j
@Component
public class TransLikeFromRedis2DB {
    @Autowired
    private LikeService likeService;
    @Autowired
    private QuestionService questionService;

    @Scheduled(fixedRate = 1000 * 60 * 30)
    void transLikeFromRedis2DB(){
        log.info("======= 点赞数据持久化定时任务开始，时间：{} ======", new Date());
        Integer row1 = likeService.updateLikeFromRedis();
        Integer row2 = questionService.updateLikeCountFromRedis();
        log.info("======= 点赞数据持久化定时任务处理点赞详情{}, 处理点赞数{} ======", row1, row2);
        log.info("======= 点赞数据持久化定时任务结束，时间：{} ======", new Date());

    }
}
