package cug.cs.codercommunity.service.impl;

import cug.cs.codercommunity.dto.GithubUser;
import cug.cs.codercommunity.exception.CustomException;
import cug.cs.codercommunity.exception.CustomStatus;
import cug.cs.codercommunity.mapper.UserMapper;
import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUserByAccountId(String accountId) {
        return userMapper.selectByAccountId(accountId);
    }

    @Override
    public User addUser(GithubUser githubUser, String token) {
        User user = new User();
        user.setToken(token);
        user.setName(githubUser.getName());
        user.setAccountId(String.valueOf(githubUser.getId()));
        user.setGmtCreate(new Date());
        user.setGmtModified(user.getGmtCreate());
        user.setAvatarUrl(githubUser.getAvatar_url());
        int rows = userMapper.insert(user);
        if (rows != 1){
            throw new CustomException(CustomStatus.INSERT_FAILED);
        }
        return user;
    }

    @Override
    public User updateUser(GithubUser githubUser, String token, User user){
        user.setToken(token);
        user.setName(githubUser.getName());
        user.setGmtModified(new Date());
        user.setAvatarUrl(githubUser.getAvatar_url());
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User findUserByToken(String token) {
        return userMapper.selectByToken(token);
    }
}
