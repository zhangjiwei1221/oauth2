package cn.butterfly.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * oauth2 返回 token 信息
 *
 * @author zjw
 * @date 2021-11-20
 */
@Data
public class Oauth2Token {

    /**
     * token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * token 类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 过期时间
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * 权限范围
     */
    private String scope;

    /**
     * token 唯一标识
     */
    private String jti;

}
