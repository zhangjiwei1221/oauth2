package cn.butterfly.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

import static cn.butterfly.common.constant.BaseConstants.ERROR_PAGE;
import static cn.butterfly.common.constant.BaseConstants.REDIRECT_FAILED;

/**
 * 自定义异常
 *
 * @author zjw
 * @date 2020-08-17
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 出错重定向到错误界面
	 */
	@ExceptionHandler(Exception.class)
	public void error(HttpServletResponse response) {
		try {
			response.sendRedirect(ERROR_PAGE);
		} catch (Exception e) {
			throw new ApiException(REDIRECT_FAILED);
		}
	}

}
