package com.houwenke.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.houwenke.seckill.config.AccessLimit;
import com.houwenke.seckill.entity.Order;
import com.houwenke.seckill.entity.SeckillMessage;
import com.houwenke.seckill.entity.SeckillOrder;
import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.exception.GlobalException;
import com.houwenke.seckill.rabbitmq.MQSender;
import com.houwenke.seckill.service.GoodsService;
import com.houwenke.seckill.service.OrderService;
import com.houwenke.seckill.service.SeckillOrderService;
import com.houwenke.seckill.utils.JsonUtil;
import com.houwenke.seckill.vo.GoodsVo;
import com.houwenke.seckill.vo.RespBean;
import com.houwenke.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;
    //内存标记，减少redis的访问
    private Map<Long, Boolean> EmptyStockMap=new HashMap<>();
    //秒杀
    @RequestMapping("/doSecKill")
    public String doSeckill(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (secKillOrder != null) {
            model.addAttribute("errmag", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    @PostMapping("/{path}/doSecKill1")
    @ResponseBody
    public RespBean doSeckill1(@PathVariable String path, User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check=orderService.checkPath(user,goodsId,path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //通过内存标记减少redis的访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存操作
//        Long stock = valueOperations.decrement("secKillGoods:" + goodsId);
        Long stock=(Long)redisTemplate.execute(script, Collections.singletonList("secKillGoods:"+goodsId),Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
//            valueOperations.increment("secKillGoods" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        //异步处理，可以做到流量削峰
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if (goods.getStockCount() < 1) {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//        //判断是否重复抢购
////        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order" + user.getId() + ":" + goods.getId());
//        if(seckillOrder!=null){
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//        Order order=orderService.secKill(user,gooGet;
//        return RespBean.success(order);
    }
//orderId:成功：-1；秒杀失败，0；排队中
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId=seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @GetMapping("/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5秒内访问5次
//        String uri=request.getRequestURI();
//        captcha="0";
//        Integer count=(Integer)valueOperations.get(uri+":"+user.getId());
//        if (count==null){
//            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
//        }else if(count<5){
//            valueOperations.increment(uri+":"+user.getId());
//        }else{
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
//        }
        boolean check=orderService.checkCaptcha(user,goodsId,captcha);
        if (!check){
            RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String str=orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    @ApiOperation("获取验证码")
    @GetMapping(value = "/captcha")
    public void verifyCode(User tUser, Long goodsId, HttpServletResponse response) {
        if (tUser == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + tUser.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }


    //初始化可以执行的一些方法,把商品库存数量加载到Redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("secKillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
