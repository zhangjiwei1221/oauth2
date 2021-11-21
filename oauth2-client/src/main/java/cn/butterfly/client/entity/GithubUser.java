package cn.butterfly.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * github 用户
 *
 * @author zjw
 * @date 2021-10-23
 */
@Data
public class GithubUser {

    /**
     * 用户 id
     */
    private String id;

    /**
     * 用户名
     */
    @JsonProperty("login")
    private String username;

    /**
     * 头像
     */
    @JsonProperty("avatar_url")
    private String avatar;

}
