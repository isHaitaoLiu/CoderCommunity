package cug.cs.codercommunity.schedule;

import cug.cs.codercommunity.service.CommentService;
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
    @Autowired
    private CommentService commentService;

    @Scheduled(fixedRate = 1000 * 60 * 30)
    void transLikeFromRedis2DB(){
        log.info("======= 点赞数据持久化定时任务开始，时间：{} ======", new Date());
        Integer row1 = likeService.updateLikeFromRedis();
        Integer row2 = questionService.updateLikeCountFromRedis();
        Integer row3 = likeService.updateCommentLikeFromRedis();
        Integer row4 = commentService.updateCommentLikeCountFromRedis();
        log.info("======= 点赞数据持久化定时任务处理问题点赞详情数{}, 处理问题点赞数{}，处理评论点赞详情数{}, 处理评论点赞数{} ======", row1, row2, row3, row4);
        log.info("======= 点赞数据持久化定时任务结束，时间：{} ======", new Date());

    }
}
