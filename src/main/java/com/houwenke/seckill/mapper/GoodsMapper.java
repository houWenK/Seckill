package com.houwenke.seckill.mapper;

import com.houwenke.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houwenke.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author houwenke
 * @since 2023-04-06
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    @Select(" SELECT g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_price,g.goods_stock,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date FROM t_goods g LEFT JOIN  t_seckill_goods sg on g.id = sg.goods_id")
    List<GoodsVo> findGoodsVo();
    @Select(" SELECT g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_price,g.goods_stock,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date FROM t_goods g LEFT JOIN  t_seckill_goods sg on g.id = sg.goods_id WHERE g.id=#{goods}")
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
