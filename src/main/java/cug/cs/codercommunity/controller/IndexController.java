package cug.cs.codercommunity.controller;


import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String testController(@CookieValue(name = "token", required = false) Cookie token,
                                 HttpSession session){
        if (token != null){
            User user = userService.findUserByToken(token.getValue());
            if (user != null) session.setAttribute("user", user);
        }
        return "index";
    }
}
