package com.houwenke.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 秒杀订单表
 * </p>
 *
 * @author houwenke
 * @since 2023-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_seckill_order")
@ApiModel(value="SeckillOrder对象", description="秒杀订单表")
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀订单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;


}
