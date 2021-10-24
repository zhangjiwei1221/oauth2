package cn.butterfly.github.controller;

import cn.butterfly.common.base.UserType;
import cn.butterfly.common.exception.ApiException;
import cn.butterfly.common.util.JwtUtils;
import cn.butterfly.github.entity.GithubAuth;
import cn.butterfly.github.entity.GithubToken;
import cn.butterfly.github.entity.GithubUser;
import cn.butterfly.github.api.GithubOauthService;
import cn.butterfly.github.api.GithubApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static cn.butterfly.common.constant.BaseConstants.REDIRECT_FAILED;

/**
 * oauth2 认证控制器
 *
 * @author zjw
 * @date 2021-10-23
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {

    @Resource
    private GithubAuth githubAuth;

    @Resource
    private GithubApiService githubApiService;

    @Resource
    private GithubOauthService githubOauthService;

    /**
     * github 重定向地址
     *
     * @param code 临时授权码
     */
    @GetMapping("/redirect")
    public void redirect(String code, HttpServletResponse response) {
        // 获取 access_token
        String clientId = githubAuth.getClientId();
        String clientSecret = githubAuth.getClientSecret();
        GithubToken githubToken = githubOauthService.authorize(clientId, clientSecret, code);
        // 获取 github 用户信息
        String authorization = String.join(StringUtils.SPACE, githubToken.getTokenType(), githubToken.getAccessToken());
        GithubUser githubUser = githubApiService.getUserInfo(authorization);
        // 生成本地访问 token
        String token = JwtUtils.sign(githubUser.getUsername(), UserType.GITHUB.getType());
        try {
            response.sendRedirect(githubAuth.getFrontRedirectUrl() + "?token=" + token);
        } catch (IOException e) {
            throw new ApiException(REDIRECT_FAILED);
        }
    }

}
