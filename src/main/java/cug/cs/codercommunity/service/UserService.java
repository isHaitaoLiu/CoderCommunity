package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.GithubUser;
import cug.cs.codercommunity.model.User;

public interface UserService {
    public void addUser(GithubUser githubUser, String token);


    public User findUserByToken(String token);

    User getUserByAccountId(String accountId);

    void updateUser(GithubUser githubUser, String token, User user);
}
