package com.gewenjie.study.demo.controller;

import com.gewenjie.study.demo.dto.AccessTokenDTO;
import com.gewenjie.study.demo.dto.GithubUser;
import com.gewenjie.study.demo.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state){
        AccessTokenDTO accessTokenDTO =new AccessTokenDTO();    //下面这些都是为了发送post请求而设置的参数
        accessTokenDTO.setClient_id("2d70f42f4349939b8818");
        accessTokenDTO.setClient_secret("70ac4eb7e35ec0ac5db0a0041a6319c12b246c10");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
        accessTokenDTO.setState(state);
        String access_token = githubProvider.getAccessToken(accessTokenDTO);            //调用服务发送post请求
        GithubUser user = githubProvider.getUser(access_token);
        System.out.println(user.getName());
        return "index";
    }
}
