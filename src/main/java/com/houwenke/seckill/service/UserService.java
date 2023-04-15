package com.houwenke.seckill.service;

import com.houwenke.seckill.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.houwenke.seckill.vo.LoginVo;
import com.houwenke.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author houwenke
 * @since 2023-04-06
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
//    根据cookie获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    //修改用户密码
    RespBean updatePassword(String userTicket,String password,HttpServletRequest request, HttpServletResponse response);
}
