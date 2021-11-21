package cn.butterfly.resource.controller;

import cn.butterfly.resource.constant.TokenConstants;
import com.auth0.jwt.JWT;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源控制器
 *
 * @author zjw
 * @date 2021-11-20
 */
@RestController
public class ResourceController {

    /**
     * 根据 token 获取用户名
     *
	 * @param authorization token 请求头
     * @return 用户名
     */
    @GetMapping("/user/info")
    public String info(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.substring(TokenConstants.BEARER_PREFIX.length());
        return JWT.decode(token).getClaim(TokenConstants.USERNAME).asString();
    }

}
