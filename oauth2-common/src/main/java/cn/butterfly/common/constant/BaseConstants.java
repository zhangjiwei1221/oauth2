package cn.butterfly.common.constant;

/**
 * 基础常量类
 *
 * @author zjw
 * @date 2021-10-23
 */
public class BaseConstants {

    public static final String USERNAME = "username";

    public static final String TYPE = "type";

    public static final String SECRET = "butterfly";

    public static final int SUCCESS = 200;

    public static final int ERROR = 500;

    public static final String LOGIN_FAILED = "用户名或密码错误";

    public static final String DEFAULT_PASSWORD = "$2a$10$KvJWyf4wI.YcpzmbYGw8NOSlauim7dF9b/VSMOomONJf40Bq8F4Me";

    public static final String USERNAME_CANT_EMPTY = "用户名不可为空";

    public static final String PASSWORD_CANT_EMPTY = "密码不可为空";

    public static final String DEFAULT_MESSAGE = "一切ok";

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String ALL_PATTERN = "/**";

    public static final String LOGIN_PATH = "/login";

    public static final String OAUTH_PATH = "/oauth/**";

    public static final String ERROR_PATH = "/error";

    public static final String ALL = "*";

    public static final String AUTHENTICATION_FAILED = "身份验证失败";

    public static final String REDIRECT_FAILED = "重定向失败";

    public static final String ERROR_PAGE = "http://localhost/error";

    public static final String UTF_8 = "UTF-8";

    public static final String JSON_TYPE = "application/json;charset=utf-8";

    public static final String BASIC_TYPE = "Basic ";

    private BaseConstants() {}

}
