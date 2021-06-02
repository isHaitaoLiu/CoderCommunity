package cug.cs.codercommunity.service;

import cug.cs.codercommunity.model.User;

public interface QuestionService {
    public void creatQuestion(String title, String description, String tag, User user);
}
