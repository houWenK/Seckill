package com.houwenke.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author houwenke
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID,手机号码")
    private Long id;

    private String nickname;

    @ApiModelProperty(value = "MD5(MD5(pass明文+固定salt)+salt)")
    private String password;

    private String salt;

    @ApiModelProperty(value = "头像")
    private String head;

    @ApiModelProperty(value = "注册时间")
    private LocalDateTime registerDate;

    @ApiModelProperty(value = "最后一次登录事件")
    private LocalDateTime lastLoginDate;

    @ApiModelProperty(value = "登录次数")
    private Integer loginCount;


}
