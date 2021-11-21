package cn.butterfly.client.api;

import cn.butterfly.client.entity.GithubUser;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import org.springframework.http.HttpHeaders;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * github 接口服务类
 *
 * @author zjw
 * @date 2021-10-23
 */
@RetrofitClient(baseUrl = "https://api.github.com")
public interface GithubApiService {

    /**
     * 根据 access_token 获取 github 用户信息
     *
	 * @param authorization 请求认证头
     * @return github 用户
     */
    @GET("/user")
    GithubUser getUserInfo(@Header(HttpHeaders.AUTHORIZATION) String authorization);

}
