package cug.cs.codercommunity.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.Notification;
import cug.cs.codercommunity.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    @Select("select * from notification where receiver = #{receiver} limit #{offset}, #{size}")
    List<Notification> selectOnePageByReceiver(Integer receiver, Integer offset, Integer size);
}
