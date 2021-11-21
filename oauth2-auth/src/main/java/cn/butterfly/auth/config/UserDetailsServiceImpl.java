package cn.butterfly.auth.config;

import cn.butterfly.common.constant.BaseConstants;
import cn.butterfly.common.entity.User;
import cn.butterfly.common.exception.ApiException;
import cn.butterfly.common.service.IUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

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
                account, user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(account)));
    }

}
