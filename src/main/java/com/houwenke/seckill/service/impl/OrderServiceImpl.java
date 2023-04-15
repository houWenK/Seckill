package com.houwenke.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.houwenke.seckill.entity.Order;
import com.houwenke.seckill.entity.SeckillGoods;
import com.houwenke.seckill.entity.SeckillOrder;
import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.exception.GlobalException;
import com.houwenke.seckill.mapper.OrderMapper;
import com.houwenke.seckill.service.GoodsService;
import com.houwenke.seckill.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houwenke.seckill.service.SeckillGoodsService;
import com.houwenke.seckill.service.SeckillOrderService;
import com.houwenke.seckill.utils.MD5Util;
import com.houwenke.seckill.utils.UUIDUtil;
import com.houwenke.seckill.vo.GoodsVo;
import com.houwenke.seckill.vo.OrderDetailVo;
import com.houwenke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods secKillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        secKillGoods.setStockCount(secKillGoods.getStockCount() - 1);
//        boolean secKillGoodsResult=seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count", secKillGoods.getStockCount()).eq("id", secKillGoods.getId()).gt("stock_count", 0));
//        seckillGoodsService.updateById(secKillGoods);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=" + "stock_count-1").eq("goods_id", goods.getId()).gt("stock_count", 0));
        if (secKillGoods.getStockCount() < 1) {
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(secKillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }

    //订单详情
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }
//校验秒杀地址
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user==null||goodsId<0|| StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath=(String)redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);
        return path.equals(redisPath);
    }

    //校验验证码
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user==null|| StringUtils.isEmpty(captcha)){
            return false;
        }
        String redisCaptcha = (String)redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
