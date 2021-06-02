package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.GithubUser;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserMapper userMapper;

    @Override
    public void addUser(GithubUser githubUser, String token) {
        User user = new User();
        user.setToken(token);
        user.setName(githubUser.getName());
        user.setAccountId(String.valueOf(githubUser.getId()));
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        user.setAvatarUrl(githubUser.getAvatar_url());
        userMapper.insertUser(user);
    }

    @Override
    public User findUserByToken(String token) {
        return userMapper.selectUserByToken(token);
    }
}
