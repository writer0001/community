package com.gewenjie.study.demo.controller;

import com.gewenjie.study.demo.dto.AccessTokenDTO;
import com.gewenjie.study.demo.dto.GithubUser;
import com.gewenjie.study.demo.mapper.UserMapper;
import com.gewenjie.study.demo.model.User;
import com.gewenjie.study.demo.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

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

    @Autowired
    private UserMapper userMapper;


    //我们在这里接受了身份验证通过后传回来code和state用于后面请求access_token
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                            HttpServletResponse response){
        AccessTokenDTO accessTokenDTO =new AccessTokenDTO();    //下面这些都是为了发送post请求而设置的参数
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String access_token = githubProvider.getAccessToken(accessTokenDTO);//调用服务发送post请求，得到access_token
        GithubUser githubUser = githubProvider.getUser(access_token);             //通过access_token得到用户信息
        if(githubUser != null){ //如果不为空说明登录成功了
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";   //重定向到主页，URL会变。
        }else {
            return "redirect:/";
        }
        /*
        * session和cookie原理解释：
        * session就像是银行中的账户，cookie就像是我们手中的银行卡。
        * 每次我们去银行取钱的时候，都需要银行卡。所以当我们登录成功，再次去服务器请求一些数据的时候，浏览器就会把银行卡（cookie）
        * 发送到银行中（服务器），银行（服务器）会对比账户（session）中的数据和银行卡中的数据（cookie）若是一致，就会返回钱（数据）。
        * 上面我们只是在银行（服务器）中创建了一个账户，并没有给用户（浏览器）派发银行卡（cookie），这时候他就会自动给用户（浏览器）
        * 派发银行卡（cookie）
        * */
    }
}
