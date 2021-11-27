package cug.cs.codercommunity.provider;


import com.alibaba.fastjson.JSON;
import cug.cs.codercommunity.dto.AccessTokenDto;
import cug.cs.codercommunity.dto.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GithubProvider {
    @Value("${github.client.access-token-url}")
    private String accessTokenUrl;

    @Value("${github.client.user-info-url}")
    private String userInfoUrl;

    /*
     * @Author sakura
     * @Description 获取accessToken
     * @Date 2021/11/28
     * @Param [accessTokenDto]
     * @return java.lang.String
     **/
    public String getAccessToken(AccessTokenDto accessTokenDto){
        WebClient webClient = WebClient
                .builder()
                .baseUrl(accessTokenUrl)   //设置基本url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //设置请求类型
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .build();
        Mono<String> stringMono = webClient
                .post()
                .body(Mono.just(accessTokenDto), AccessTokenDto.class) //通过实体类构造body数据
                .retrieve()   //获取响应实体，exchange()方法可获取全部响应结果
                .bodyToMono(String.class);
        String response = stringMono.block();
        log.info("assessToken返回结果：[{}]", response);
        if (response != null) {
            return response.split("&")[0].split("=")[1]; //截取access_token
        }
        return null;
    }

    /*
     * @Author sakura
     * @Description 拿着access_token去获取用户信息
     * @Date 2021/11/28
     * @Param [accessToken]
     * @return cug.cs.codercommunity.dto.GithubUser
     **/
    public GithubUser getGithubUser(String accessToken){
        WebClient webClient = WebClient
                .builder()
                .baseUrl(userInfoUrl)
                .build();
        Mono<String> response = webClient
                .get()
                .header("Authorization", "token " + accessToken)  //请求header构造
                .retrieve().bodyToMono(String.class);
        GithubUser githubUser = JSON.parseObject(response.block(), GithubUser.class); //Json化，存储到实体类
        log.info("userInfo返回结果：[{}]", githubUser);
        return githubUser;
    }
}
