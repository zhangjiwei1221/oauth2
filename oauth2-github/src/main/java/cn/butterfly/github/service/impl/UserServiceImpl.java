package cn.butterfly.github.service.impl;

import cn.butterfly.common.base.UserType;
import cn.butterfly.github.entity.User;
import cn.butterfly.github.service.IUserService;
import org.springframework.stereotype.Service;
import static cn.butterfly.common.constant.BaseConstants.DEFAULT_PASSWORD;

/**
 * 用户服务实现类
 *
 * @author zjw
 * @date 2021-09-12
 */
@Service
public class UserServiceImpl implements IUserService {

    @Override
    public User getByUsername(String username) {
        return new User(username, DEFAULT_PASSWORD, UserType.DEFAULT.getType());
    }

}
