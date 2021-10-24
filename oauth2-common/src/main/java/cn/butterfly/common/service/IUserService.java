package cn.butterfly.common.service;

import cn.butterfly.common.entity.User;

/**
 * 用户服务类
 *
 * @author zjw
 * @date 2021-09-12
 */
public interface IUserService {

    /**
     * 根据用户名查找用户
     *
	 * @param username 用户名
     * @return 用户
     */
    User getByUsername(String username);

}
