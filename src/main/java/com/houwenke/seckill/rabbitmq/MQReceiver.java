package com.houwenke.seckill.rabbitmq;


import com.houwenke.seckill.entity.SeckillMessage;
import com.houwenke.seckill.entity.SeckillOrder;
import com.houwenke.seckill.entity.User;
import com.houwenke.seckill.service.GoodsService;
import com.houwenke.seckill.service.OrderService;
import com.houwenke.seckill.utils.JsonUtil;
import com.houwenke.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/**
 * 消息消费者
 *
 * @author: LC
 * @date 2022/3/7 7:44 下午
 * @ClassName: MQReceiver
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;


    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收到的消息:"+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount()<1){
            return;
        }
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return ;
        }
        orderService.secKill(user,goodsVo);
    }






//    @RabbitListener(queues = "queue")
//    public void receive(Object msg) {
//        System.out.println("接收到的消息" + msg);
//    }
//
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header01")
//    public void receive07(Message message) {
//        log.info("QUEUE01接收消息 message对象" + message);
//        log.info("QUEUE01接收消息" + new String(message.getBody()));
//    }
//
//    @RabbitListener(queues = "queue_header02")
//    public void receive08(Message message) {
//        log.info("QUEUE02接收消息 message对象" + message);
//        log.info("QUEUE02接收消息" + new String(message.getBody()));
//    }

}
