package cn.butterfly.github.controller;

import cn.butterfly.common.base.BaseResult;
import cn.butterfly.common.base.UserType;
import cn.butterfly.common.entity.User;
import cn.butterfly.common.service.IUserService;
import cn.butterfly.common.util.JwtUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.Objects;
import static cn.butterfly.common.constant.BaseConstants.LOGIN_FAILED;

/**
 * 登录控制器
 *
 * @author zjw
 * @date 2021-10-23
 */
@RestController
public class LoginController {

    @Resource
    private IUserService userService;

    /**
     * 用户登录
     *
	 * @param user 用户
     * @return 结果
     */
    @PostMapping("/login")
    public BaseResult<String> login(@RequestBody @Validated User user) {
        String username = user.getUsername();
        User resultUser = userService.getByUsername(username);
        if (resultUser == null || !Objects.equals(resultUser.getPassword(), user.getPassword())) {
            return BaseResult.error(LOGIN_FAILED);
        }
        String token = JwtUtils.sign(username, UserType.DEFAULT.getType());
        return BaseResult.success(token);
    }

}
