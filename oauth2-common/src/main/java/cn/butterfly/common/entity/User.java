package cn.butterfly.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static cn.butterfly.common.constant.BaseConstants.PASSWORD_CANT_EMPTY;
import static cn.butterfly.common.constant.BaseConstants.USERNAME_CANT_EMPTY;

/**
 * 用户实体类
 *
 * @author zjw
 * @date 2021-09-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户名
     */
    @NotBlank(message = USERNAME_CANT_EMPTY)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = PASSWORD_CANT_EMPTY)
    private String password;

    /**
     * 用户类别
     */
    private Integer type;

}
