package cn.butterfly.common.base;

import lombok.Getter;

/**
 * 用户类别枚举类
 *
 * @author zjw
 * @date 2021-10-23
 */
@Getter
public enum UserType {

    /**
     * 默认登录方式
     */
    DEFAULT(0),

    /**
     * github 登录方式
     */
    GITHUB(1),

    /**
     * 自定义 oauth2 登录方式
     */
    OAUTH2(2);

    /**
     * 用户类别
     */
    private final Integer type;

    UserType(Integer type) {
        this.type = type;
    }

}
