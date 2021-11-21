# GitHub OAuth2 第三方登录及自定义认证服务器的实现

## 前言

本文将介绍如何访问基于`OAuth2`协议的`GitHub`用户信息`API`接口以及如何自己实现一个简单的基于授权码模式的认证服务器，如果对`OAuth2`的基本概念和四种授权模式还不熟悉，可以先看一下阮一峰老师的博客：[OAuth 2.0 的一个简单解释](https://www.ruanyifeng.com/blog/2019/04/oauth_design.html)，本文则主要以实际的`demo`来讲解使用方法。本文所展示示例的完整代码已上传到[GitHub](https://github.com/zhangjiwei1221/oaouth2)。



## GitHub 第三方登录

### 前置准备

在访问`Github`的`API`接口之前，需要先访问`https://github.com/settings/applications/new`，然后填写以下的内容：

![image-20211121161853496](https://gitee.com/zhangjiwei1221/note/raw/master/img/20211121161900.png)

这里除了最后一项`Authorization callback URL`，其它内容对后续的代码处理都没有影响(用于用户点击第三方登录时展示网站的信息)，而最后一项则是用于接收临时授权码`code`来换取`Access Token`的回调地址，即对应下图中的`D`和`E`，下图来自`https://datatracker.ietf.org/doc/html/rfc6749#section-4.1`。

```
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
       |    |                                         ^      v
      (A)  (C)                                        |      |
       |    |                                         |      |
       ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)
     
     Note: The lines illustrating steps (A), (B), and (C) are broken into two parts as they pass through the user-agent.
                          Authorization Code Flow
```

在填完以上信息后，就会跳转到以下界面：

![image-20211121163154306](https://gitee.com/zhangjiwei1221/note/raw/master/img/20211121163154.png)

这里需要将`Client ID`以及点击`Generate a new client secret`后生成的`Client Secret`进行保存用于后续使用。

### 编码

完成以上的准备步骤后就可以开始编码工作了，首先为了后续使用和修改方便，可以先将`Client ID`和`Client Secret`在配置文件中进行配置(为了减少篇幅，只展示部分核心代码)：

```yaml
server:
  port: 8080

oauth:
  github:
    # 替换为自己的 Client ID 和 Client Secret
    clientId: 8aed0bc8316548e9d26d
    clientSecret: fdd0e7af5052164e459098703005c5db25f857a8
    # 用于后台获取 GitHub 用户信息后生成本地 token 后传递给前端处理的地址 
    frontRedirectUrl: http://localhost/redirect

# 一个 HTTP客户端(https://github.com/LianjiaTech/retrofit-spring-boot-starter)框架的配置
retrofit:
  global-connect-timeout-ms: 20000
  global-read-timeout-ms: 10000
```

然后编写对应的实体`GithubAuth`：

```java
/**
 * github 认证信息
 *
 * @author zjw
 * @date 2021-10-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth.github")
public class GithubAuth {

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
```

然后编写`GitHub`的认证接口服务类：

```java
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
    GithubToken getToken(
        @Query("client_id") String clientId, 
        @Query("client_secret") String clientSecret, 
        @Query("code") String code);

}
```

以及获取用户信息的接口服务类：

```java
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
```

然后是处理临时授权码`code`的接口(这里的接口地址即对应上文中填写的回调地址)：

```java
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
    private GithubAuthService githubAuthService;

    /**
     * github 重定向地址
     *
     * @param code 临时授权码
     * @param response 响应
     */
    @GetMapping("/github/redirect")
    public void githubRedirect(String code, HttpServletResponse response) {
        // 获取 access_token
        String clientId = githubAuth.getClientId();
        String clientSecret = githubAuth.getClientSecret();
        GithubToken githubToken = githubAuthService.getToken(clientId, clientSecret, code);
        // 获取 github 用户信息
        String authorization = String.join(
            StringUtils.SPACE, githubToken.getTokenType(), githubToken.getAccessToken());
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
```

而前端只需要在登录首页放置对应的`GitHub`图标并设置点击事件：

```javascript
githubAuthorize() {
	const env = process.env
	window.location.href = `https://github.com/login/oauth/authorize?
      	client_id=${env.VUE_APP_GITHUB_CLIENT_ID}
      	&redirect_uri=${env.VUE_APP_GITHUB_REDIRECT_URI}`
}
```

并且在重定向界面进行以下跳转处理：

```javascript
created() {
	this.setToken(this.$route.query.token)
    this.$router.push('/')
}
```

以上便是项目的核心配置，最终效果如下：

![动画](https://gitee.com/zhangjiwei1221/note/raw/master/img/20211121171425.gif)

## 自己实现 OAuth2 认证服务器

### 前置准备

本文采用了数据库存储的方式用来保存客户端的信息，因此首先需要执行以下`SQL`脚本创建对应的表：

```sql
-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `resource_ids` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `client_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `scope` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authorized_grant_types` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `web_server_redirect_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authorities` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `access_token_validity` int(11) NULL DEFAULT NULL,
  `refresh_token_validity` int(11) NULL DEFAULT NULL,
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `autoapprove` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('butterfly', NULL, '$2a$10$KvJWyf4wI.YcpzmbYGw8NOSlauim7dF9b/VSMOomONJf40Bq8F4Me', 'all', 'authorization_code', 'http://localhost:8080/oauth/oauth2/redirect', NULL, 3600, 7200, NULL, 'false');
```

### 编码

首先是`yaml`配置：

```yaml
server:
  port: 9002

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    jdbcUrl: jdbc:mysql://localhost:3306/oauth2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: root
```

然后是认证服务器的配置：

```java
/**
 * 认证服务器配置
 *
 * @author zjw
 * @date 2021-10-13
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 配置 jwt 类型的 access_token
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 设置 token 签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey(BaseConstants.SECRET);
        return accessTokenConverter;
    }

    /**
     * 配置认证数据源
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置 jdbc 认证方式
     */
    @Bean
    public ClientDetailsService jdbcClientDetails() {
        return new JdbcClientDetailsService(dataSource());
    }

    /**
     * 配置 jdbc 认证方式
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetails());
    }

    /**
     * 配置获取凭证的信息格式及内容
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                 // 获取 refresh_token 需要配置此项
                 .userDetailsService(userDetailsService);
    }

    /**
     * 开启 token 认证功能
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()");
    }

}
```

然后是自定义的用户权限信息，在这里可以设置`token`中保存的用户和权限相关信息：

```java
/**
 * 用户权限信息设置
 *
 * @author zjw
 * @date 2021-10-13
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private IUserService userService;

    /**
     * 设置 access_token 中存储的认证信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new ApiException(BaseConstants.AUTHENTICATION_FAILED);
        }
        String account = user.getUsername();
        return new org.springframework.security.core.userdetails.User(
            account, user.getPassword(), 
            Collections.singletonList(new SimpleGrantedAuthority(account))
        );
    }

}
```

然后是`Web`安全相关的配置：

```java
/**
 * web 安全配置
 *
 * @author zjw
 * @date 2021-10-13
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * 密码加密方式
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义用户权限配置
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    /**
     * 设置 token 中存储的权限信息
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.POST.name(), HttpMethod.GET.name(), HttpMethod.OPTIONS.name()));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ALL_PATTERN, configuration);
        return source;
    }

}
```

以上便是授权服务器全部的配置，下面展示资源服务器相关的配置，这里仍然采取将授权和资源服务器分开的方式进行配置，并且采用的仍然是老版本的配置方法：

```java
/**
 * 资源服务器
 *
 * @author zjw
 * @date 2021-11-21
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .exceptionHandling()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/user/**").access("#oauth2.hasScope('all')");
    }

}
```

然后创建一个返回用户信息的接口：

```java
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
```

`yaml`的配置如下，这里的`http://localhost:9002`需要修改为自己授权服务器的地址：

```yaml
server:
  port: 9003

security:
  oauth2:
    client:
      client-id: butterfly
      client-secret: 123456
      access-token-uri: http://localhost:9002/oauth/token
      user-authorization-uri: http://localhost:9002/oauth/authorize
    resource:
      token-info-uri: http://localhost:9002/oauth/check_token
```

完成以上的配置后便可以参考`GitHub`中步骤来编写客户端的后端代码：

首先是`yaml`的配置：

```yaml
server:
  port: 8080

oauth:
  oauth2:
    clientId: butterfly
    clientSecret: 123456
    frontRedirectUrl: http://localhost/redirect

retrofit:
  global-connect-timeout-ms: 20000
  global-read-timeout-ms: 10000
```

然后创建对应的实体：

```java
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
```

然后编写对应的认证及获取用户信息的接口服务类：

```java
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
    Oauth2Token getToken(
        @Header(HttpHeaders.AUTHORIZATION) String authorization, 
        @Query("code") String code,
        @Query("grant_type") String grantType);

}
```

```java
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
```

然后编写处理临时授权码`code`的回调接口：

```java
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
    private Oauth2Auth oauth2Auth;
    
    @Resource
    private Oauth2AuthService oauth2AuthService;

    @Resource
    private Oauth2ApiService oauth2ApiService;

    /**
     * oauth2 重定向地址
     *
     * @param code 临时授权码
     * @param response 响应
     */
    @GetMapping("/oauth2/redirect")
    public void oauth2Redirect(String code, HttpServletResponse response) {
        // 获取 access_token
        String clientId = oauth2Auth.getClientId();
        String clientSecret = oauth2Auth.getClientSecret();
        String authorization = BaseConstants.BASIC_TYPE + Base64.getEncoder().encodeToString(
                (String.join(":", clientId, clientSecret)).getBytes()
        );
        // 获取 oauth2 用户名
        Oauth2Token oauth2Token = oauth2AuthService.getToken(
            authorization, code, "authorization_code");
        String username = oauth2ApiService.getUserInfo(
                String.join(
                    StringUtils.SPACE, 
                    oauth2Token.getTokenType(), 
                    oauth2Token.getAccessToken()
                )
        );
        // 生成本地访问 token
        String token = JwtUtils.sign(username, UserType.OAUTH2.getType());
        try {
            response.sendRedirect(oauth2Auth.getFrontRedirectUrl() + "?token=" + token);
        } catch (IOException e) {
            throw new ApiException(REDIRECT_FAILED);
        }
    }

}
```

然后在前端同样添加对应图标的点击事件：

```javascript
oauth2Authorize() {
	const env = process.env
	window.location.href = `http://localhost:9002/oauth/authorize?
      	client_id=${env.VUE_APP_OAUTH_CLIENT_ID}&response_type=code`
}
```

重定向界面的跳转处理仍然不变：

```javascript
created() {
	this.setToken(this.$route.query.token)
    this.$router.push('/')
}
```

最终的效果如下：

![动画](https://gitee.com/zhangjiwei1221/note/raw/master/img/20211121184320.gif)


