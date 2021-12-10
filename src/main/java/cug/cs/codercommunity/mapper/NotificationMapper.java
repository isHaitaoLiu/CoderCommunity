package cug.cs.codercommunity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    List<Notification> selectOnePageByReceiver(Integer receiver, Integer offset, Integer size);
}
