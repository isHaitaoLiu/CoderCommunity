package cug.cs.codercommunity.service;

import cug.cs.codercommunity.dto.GithubUser;
import cug.cs.codercommunity.model.User;

public interface UserService {
    User addUser(GithubUser githubUser, String token);

    User findUserByToken(String token);

    User getUserByAccountId(String accountId);

    User updateUser(GithubUser githubUser, String token, User user);
}
