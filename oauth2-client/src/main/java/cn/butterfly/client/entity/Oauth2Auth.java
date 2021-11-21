package cn.butterfly.client.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * oauth2 认证信息
 *
 * @author zjw
 * @date 2021-10-31
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth.oauth2")
public class Oauth2Auth {

    /**
     * 客户端 id
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 前端重定向地址
     */
    private String frontRedirectUrl;

}
