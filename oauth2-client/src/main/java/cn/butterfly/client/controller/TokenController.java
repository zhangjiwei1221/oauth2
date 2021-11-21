package cn.butterfly.client.controller;

import cn.butterfly.common.base.BaseResult;
import cn.butterfly.common.util.JwtUtils;
import cn.hutool.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * token 解析控制器
 *
 * @author zjw
 * @date 2021-10-23
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    @GetMapping("/github/info")
    public BaseResult<JSONObject> info(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        JSONObject userJson = JwtUtils.getUser(token);
        return BaseResult.success(userJson);
    }

}
