package com.gewenjie.study.demo.provider;

import com.alibaba.fastjson.JSON;
import com.gewenjie.study.demo.dto.AccessTokenDTO;
import com.gewenjie.study.demo.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;




/*这个注解的意思就是把这类给交给springboot管理，在使用这个类的对象时，不用额外的new一个对象，只要定义好变量然后添加@Autowired，
springboot上下文就会自动创建对象然后将对象赋值给添加了@Autowired注释的变量
小知识：
持久层、业务层和控制层中，分别采用@Repository、@Service和@Controller对分层中的类进行凝视，
而用@Component对那些比较中立的类进行凝视*/
@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string,GithubUser.class);
            return githubUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
