package cn.butterfly.client.api;

import cn.butterfly.client.entity.Oauth2Token;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import org.springframework.http.HttpHeaders;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * oauth2 接口服务类
 *
 * @author zjw
 * @date 2021-10-31
 */
@RetrofitClient(baseUrl = "http://localhost:9002/oauth/")
public interface Oauth2AuthService {

    /**
     * 进行 oauth2 授权请求
     *
     * @param authorization 认证头
     * @param code 临时授权码
     * @param grantType 认证类型
     * @return access_token
     */
    @POST("token")
    Oauth2Token getToken(@Header(HttpHeaders.AUTHORIZATION) String authorization, @Query("code") String code,
                         @Query("grant_type") String grantType);

}
