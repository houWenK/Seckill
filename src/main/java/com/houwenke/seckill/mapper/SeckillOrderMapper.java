package com.houwenke.seckill.mapper;

import com.houwenke.seckill.entity.SeckillOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 秒杀订单表 Mapper 接口
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

}
