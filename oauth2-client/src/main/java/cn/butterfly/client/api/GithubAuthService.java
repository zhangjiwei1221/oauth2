package cn.butterfly.client.api;

import cn.butterfly.client.entity.GithubToken;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * github oauth 接口服务类
 *
 * @author zjw
 * @date 2021-10-23
 */
@RetrofitClient(baseUrl = "https://github.com/login/oauth/")
public interface GithubAuthService {

    /**
     * 进行 github 授权请求
     *
     * @param clientId 客户端 id
     * @param clientSecret 客户端密钥
     * @param code 临时授权码
     * @return access_token
     */
    @POST("access_token")
    @Headers("Accept: application/json")
    GithubToken getToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("code") String code);

}
