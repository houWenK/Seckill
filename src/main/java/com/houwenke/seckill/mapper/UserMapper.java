package com.houwenke.seckill.mapper;

import com.houwenke.seckill.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author houwenke
 * @since 2023-04-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
