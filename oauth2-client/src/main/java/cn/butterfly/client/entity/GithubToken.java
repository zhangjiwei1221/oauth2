package cn.butterfly.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * github 授权 token 对象
 *
 * @author zjw
 * @date 2021-10-23
 */
@Data
public class GithubToken {

    /**
     * 授权 token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * token 类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 访问范围
     */
    private String scope;

}
