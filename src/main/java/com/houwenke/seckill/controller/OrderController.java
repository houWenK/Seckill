package com.houwenke.seckill.controller;


import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.service.OrderService;
import com.houwenke.seckill.vo.OrderDetailVo;
import com.houwenke.seckill.vo.RespBean;
import com.houwenke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
    OrderDetailVo detail=orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
