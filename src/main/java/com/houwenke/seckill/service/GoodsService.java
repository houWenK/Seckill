package com.houwenke.seckill.service;

import com.houwenke.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.houwenke.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author houwenke
 * @since 2023-04-06
 */
public interface GoodsService extends IService<Goods> {

   List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
