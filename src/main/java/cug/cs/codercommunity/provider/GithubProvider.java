package cug.cs.codercommunity.Provider;


import com.alibaba.fastjson.JSON;
import cug.cs.codercommunity.dto.AccessTokenDto;
import cug.cs.codercommunity.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDto accessTokenDto){
        MediaType mediaType
                = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.Companion.create(JSON.toJSONString(accessTokenDto), mediaType);
        //RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDto));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //ResponseBody body1 = response.body();
            String string = response.body().string();
            String[] strings = string.split("&");
            String[] tokens = strings[0].split("=");
            String res = tokens[1];
            System.out.println(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getGithubUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();
            GithubUser githubUser = JSON.parseObject(s, GithubUser.class);
            return githubUser;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
