package cn.butterfly.common.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import static cn.butterfly.common.constant.BaseConstants.*;

/**
 * jwt 工具类
 *
 * @author zjw
 * @date 2021-09-12
 */
public class JwtUtils {

    /**
     * 过期时间 7 天(毫秒)
     */
    private static final long EXPIRE_TIME = (long) 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成 token
     *
     * @param username 用户名
     * @param type 用户类别
     * @return token
     */
    public static String sign(String username, Integer type) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return createToken(username, type, date);
    }

    /**
     * 生成 token(包含用户名信息)
     *
     * @param username 用户名
     * @param type 用户类别
     * @param date 有效期
     * @return token
     */
    private static String createToken(String username, Integer type, Date date) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim(USERNAME, username)
                .withClaim(TYPE, type)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 校验 token 是否合法
     *
     * @param token 密钥
     * @return 是否合法
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得 token 中保存的用户信息
     *
     * @return 用户 json 对象信息
     */
    public static JSONObject getUser(String token) {
        JSONObject jsonObject = JSONUtil.createObj();
        try {
            DecodedJWT jwt = JWT.decode(token);
            jsonObject.set(USERNAME, jwt.getClaim(USERNAME).asString());
            jsonObject.set(TYPE, jwt.getClaim(TYPE).asInt());
            return jsonObject;
        } catch (Exception e) {
            return jsonObject;
        }
    }

    private JwtUtils() {}

}
