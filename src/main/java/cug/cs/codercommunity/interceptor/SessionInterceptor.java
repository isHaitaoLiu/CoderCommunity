package cug.cs.codercommunity.interceptor;

import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.service.NotificationService;
import cug.cs.codercommunity.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)){
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    User user = userService.findUserByToken(token);
                    if (user != null){
                        request.getSession().setAttribute("user", user);
                        Integer count = notificationService.unreadCount(user.getId());
                        request.getSession().setAttribute("unreadCount", count);
                        break;
                    }
                }
            }
        }
        return true;
    }
}
