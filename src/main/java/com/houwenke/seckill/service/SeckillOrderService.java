package com.houwenke.seckill.service;

import com.houwenke.seckill.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.houwenke.seckill.entity.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
public interface SeckillOrderService extends IService<SeckillOrder> {
    //获取秒杀结果
    Long getResult(User user, Long goodsId);
}
