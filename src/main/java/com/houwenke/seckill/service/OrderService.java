package com.houwenke.seckill.service;

import com.houwenke.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.vo.GoodsVo;
import com.houwenke.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
public interface OrderService extends IService<Order> {

//秒杀
    Order secKill(User user, GoodsVo goods);
//订单详情
    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
