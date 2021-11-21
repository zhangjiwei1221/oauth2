package cn.butterfly.client.api;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import org.springframework.http.HttpHeaders;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * oauth2 接口服务类
 *
 * @author zjw
 * @date 2021-10-31
 */
@RetrofitClient(baseUrl = "http://localhost:9003")
public interface Oauth2ApiService {

    /**
     * 根据 access_token 获取 oauth2 用户名
     *
     * @param authorization 请求认证头
     * @return oauth2 用户名
     */
    @GET("/user/info")
    String getUserInfo(@Header(HttpHeaders.AUTHORIZATION) String authorization);

}
