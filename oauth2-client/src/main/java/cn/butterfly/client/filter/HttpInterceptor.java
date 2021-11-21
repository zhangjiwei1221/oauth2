package cn.butterfly.client.filter;

import cn.butterfly.common.base.BaseResult;
import cn.butterfly.common.util.JsonUtils;
import cn.butterfly.common.util.JwtUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.butterfly.common.constant.BaseConstants.*;

/**
 * 拦截器配置
 *
 * @author zjw
 * @date 2021-09-09
 */
@Configuration
public class HttpInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 放开 options 请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 设置响应配置
        response.setCharacterEncoding(UTF_8);
        response.setContentType(JSON_TYPE);

        // 校验 token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (JwtUtils.verify(token)) {
            return true;
        }
        response.getWriter().write(JsonUtils.stringify(BaseResult.error(AUTHENTICATION_FAILED)));
        return false;
    }

}
