package cug.cs.codercommunity.Controller;


import cug.cs.codercommunity.Provider.GithubProvider;
import cug.cs.codercommunity.dto.AccessTokenDto;
import cug.cs.codercommunity.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.redirecturl}")
    String redirectUrl;
    @Value("${github.client.id}")
    String clientId;
    @Value("${github.client.secret}")
    String clientSecret;

    @GetMapping("/callback")
    public String callback(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "state") String state){
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(redirectUrl);
        accessTokenDto.setState(state);
        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        GithubUser user = githubProvider.getGithubUser(accessToken);
        System.out.println(user.getName());
        return "index";
    }
}
