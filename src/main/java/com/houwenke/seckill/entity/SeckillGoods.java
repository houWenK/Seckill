package com.houwenke.seckill.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 秒杀商品表
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_seckill_goods")
@ApiModel(value="SeckillGoods对象", description="秒杀商品表")
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀商品ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "秒杀家")
    private BigDecimal seckillPrice;

    @ApiModelProperty(value = "库存数量")
    private Integer stockCount;

    @ApiModelProperty(value = "秒杀开始时间")
    private LocalDateTime startDate;

    @ApiModelProperty(value = "秒杀结束时间")
    private LocalDateTime endDate;


}
