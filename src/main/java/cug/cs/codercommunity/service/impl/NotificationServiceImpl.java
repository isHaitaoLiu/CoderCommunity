package cug.cs.codercommunity.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cug.cs.codercommunity.dto.NotificationDto;
import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.enums.NotificationStatusEnum;
import cug.cs.codercommunity.enums.NotificationTypeEnum;
import cug.cs.codercommunity.exception.CustomException;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.CommentMapper;
import cug.cs.codercommunity.mapper.NotificationMapper;
import cug.cs.codercommunity.mapper.QuestionMapper;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.model.Notification;
import cug.cs.codercommunity.model.Question;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;


    @Override
    public PageDto<NotificationDto> getOnePage(Integer page, Integer size, User user) {
        Integer totalCount;

        //通知总数
        QueryWrapper<Notification> notificationQueryWrapper = new QueryWrapper<>();
        totalCount = notificationMapper.selectCount(notificationQueryWrapper.eq("receiver", user.getId()));

        //开始为pageDto赋值
        PageDto<NotificationDto> pageDto = new PageDto<>();
        //计算总页数
        if (totalCount % size == 0){
            pageDto.setTotalPage(totalCount / size);
        } else {
            pageDto.setTotalPage(totalCount / size + 1);
        }
        //非法页校正
        if (page > pageDto.getTotalPage()) page = pageDto.getTotalPage();
        if (page < 1) page = 1;
        //记录当前页
        pageDto.setCurrentPage(page);
        //计算当前分页状态栏记录的页码
        pageDto.setPages(new ArrayList<>());
        List<Integer> pages = pageDto.getPages();
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0){
                pages.add(0, page - i);
            }
        }
        for (int i = 1; i <= 3; i++) {
            if (page + i <= pageDto.getTotalPage()){
                pages.add(page + i);
            }
        }
        //是否展示上一页、下一页符号
        pageDto.setShowPrevious(page != 1);
        pageDto.setShowNext(!page.equals(pageDto.getTotalPage()));

        //是否展示第一页、最后一页符号
        pageDto.setShowFirstPage(!pages.contains(1));
        pageDto.setShowLastPage(!pages.contains(pageDto.getTotalPage()));
        Integer offset = (page - 1) * size;

        //获取所有通知
        List<Notification> notifications = notificationMapper.selectOnePageByReceiver(user.getId(), offset, size);
        notifications.sort((a, b) -> {
            return (int) (b.getGmtCreate().compareTo(a.getGmtCreate()));
        });
        if (notifications.size() == 0){
            return pageDto;
        }
        //获取发出此通知的用户{id, name}
        Set<Integer> userIds = notifications.stream().map(Notification::getNotifier).collect(Collectors.toSet());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(userQueryWrapper.in("id", userIds));
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        //获取question{id, title}
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.in("creator", userIds);
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        Map<Integer, String> questionIdToTitle = questionList.stream().collect(Collectors.toMap(Question::getId, Question::getTitle));

        //构造传向前端页面的通知内容
        List<NotificationDto> notificationDTOList = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setId(notification.getId());
            notificationDto.setStatus(notification.getStatus());
            notificationDto.setGmtCreate(new Date());
            notificationDto.setOuterTitle(questionIdToTitle.get(notification.getOuterId()));
            notificationDto.setNotifierName(userMap.get(notification.getNotifier()).getName());
            notificationDto.setNotifier(notification.getNotifier());
            notificationDto.setOuterId(notification.getOuterId());
            notificationDto.setType(notification.getType());
            notificationDto.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOList.add(notificationDto);
        }
        //放入question列表
        pageDto.setData(notificationDTOList);
        return pageDto;
    }

    @Override
    public NotificationDto read(Integer id, User user) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null){
            throw new CustomException(CustomStatus.NOTIFICATION_NOT_FOUND);
        }

        if (!notification.getReceiver().equals(user.getId())){
            throw new CustomException(CustomStatus.READ_NOTIFICATION_FILE);
        }
        //更新状态
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateById(notification);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setType(notification.getType());
        notificationDto.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        notificationDto.setOuterId(notification.getOuterId());
        return notificationDto;
    }

    @Override
    public Integer unreadCount(Integer id) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver", id).eq("status", NotificationStatusEnum.UNREAD.getStatus());
        Integer count = notificationMapper.selectCount(queryWrapper);
        return count;
    }
}
