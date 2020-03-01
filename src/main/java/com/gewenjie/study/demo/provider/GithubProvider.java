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
    public String getAccessToken(AccessTokenDTO accessTokenDTO){//把需要的参数穿进阿狸，然后就可以发送请求，并返回access_token
        //创建一个mediatype，设置字符集和信息传输格式为json
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();//创建一个客户端，我们一会儿就是用这个来发送请求（request）
        //创建post的body，把mediatype和json格式的数据作为参数传入
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()//创建一个请求，然后设置url和body，通过build方法建立起来
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) { //通过客户端创建一个新的会话将请求传入并执行，用response接受
            String string = response.body().string();//接受返回的参数部分并付给一个字符串
            String token = string.split("&")[0].split("=")[1];//通过分割方法得到需要的token。
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //通过accesstoken得到用户信息
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();           //创建一个客户端
        Request request = new Request.Builder()             //创建一个请求，设置url
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();//用客户端建立会话发送请求，并用response接受返回来的消息
            String string = response.body().string();             //将返回来的消息转化成字符串格式
            //使用fastjson包里的parseObject方法，将返回来的信息解析成对象，放入到githuuser当中去。这里要注意的是，githubUser这个
            //dto中的属性名应该和json中的key名一样才行，没有创建的属性名，就不会赋值到githubUser当中
            GithubUser githubUser = JSON.parseObject(string,GithubUser.class);
            return githubUser;//返回用户信息
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
