package com.gewenjie.study.demo.controller;

import com.gewenjie.study.demo.dto.AccessTokenDTO;
import com.gewenjie.study.demo.dto.GithubUser;
import com.gewenjie.study.demo.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    //通过自动注入的方式，得到了githubprovider对象，下面就可以调用它的发送post请求和get请求得到用户信息的方法了。
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;


    //我们在这里接受了身份验证通过后传回来code和state用于后面请求access_token
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state){
        AccessTokenDTO accessTokenDTO =new AccessTokenDTO();    //下面这些都是为了发送post请求而设置的参数
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String access_token = githubProvider.getAccessToken(accessTokenDTO);//调用服务发送post请求，得到access_token
        GithubUser user = githubProvider.getUser(access_token);             //通过access_token得到用户信息
        System.out.println(user.getName());
        return "index";
    }
}
