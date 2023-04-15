package com.houwenke.seckill.exception;

import com.houwenke.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends  RuntimeException{
    private RespBeanEnum respBeanEnum;
}
