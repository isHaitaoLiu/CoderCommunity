package cug.cs.codercommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cug.cs.codercommunity.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User>{

    @Insert("insert into user(account_id, name, token, gmt_create, gmt_modified, avatar_url) " +
            "values(#{accountId}, #{name}, #{token}, #{gmtCreate}, #{gmtModified}, #{avatarUrl})")
    void insertUser(User user);

    @Select("select * from user where token = #{token}")
    User selectUserByToken(String token);

    @Select("select * from user where id = #{id}")
    User selectUserById(Integer id);

    @Select("select * from user where account_id = #{accountId}")
    User selectUserByAccountId(String accountId);

    @Update("update user set name = #{name}, token = #{token}, gmt_modified = #{gmtModified}, avatar_url = #{avatarUrl} where id = #{id}")
    void updateUser(User user);
}
