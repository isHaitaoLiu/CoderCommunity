package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.NotificationDto;
import cug.cs.codercommunity.dto.PageDto;
import cug.cs.codercommunity.model.User;

public interface NotificationService {
    PageDto<NotificationDto> getOnePage(Integer page, Integer size, User user);

    NotificationDto read(Integer id, User user);

    Integer unreadCount(Integer id);
}
