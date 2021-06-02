package cug.cs.codercommunity.controller;

import cug.cs.codercommunity.model.User;
import cug.cs.codercommunity.provider.GithubProvider;
import cug.cs.codercommunity.dto.AccessTokenDto;
import cug.cs.codercommunity.dto.GithubUser;
import cug.cs.codercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    //从配置文件中注入
    @Value("${github.client.redirecturl}")
    String redirectUrl;
    @Value("${github.client.id}")
    String clientId;
    @Value("${github.client.secret}")
    String clientSecret;
    @Autowired
    UserService userService;

    //github的回调处理
    @GetMapping("/callback")
    public String callback(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "state") String state,
            HttpSession session,
            HttpServletResponse response){
        AccessTokenDto accessTokenDto = new AccessTokenDto();

        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(redirectUrl);
        accessTokenDto.setState(state);
        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
        if(githubUser != null) {
            String token = UUID.randomUUID().toString();
            //用户信息写入数据库
            userService.addUser(githubUser, token);
            //构造cookie信息
            response.addCookie(new Cookie("token", token));
        }
        return "redirect:/";
    }
}
