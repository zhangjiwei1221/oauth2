package cn.butterfly.client.config;

import cn.butterfly.client.filter.HttpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

import static cn.butterfly.common.constant.BaseConstants.*;

/**
 * web 配置
 *
 * @author zjw
 * @date 2021-09-09
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

	@Resource
	private HttpInterceptor httpInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(ALL_PATTERN)
				.allowedOrigins(ALL)
				.allowedMethods(HttpMethod.POST.name(), HttpMethod.GET.name(), HttpMethod.OPTIONS.name())
				.exposedHeaders(HttpHeaders.AUTHORIZATION)
				.allowedHeaders(ALL);
	}

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(httpInterceptor)
				.addPathPatterns(ALL_PATTERN)
				.excludePathPatterns(LOGIN_PATH, OAUTH_PATH, ERROR_PATH);
		super.addInterceptors(registry);
	}

}
