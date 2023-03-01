package com.ljt.study.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LiJingTang
 * @date 2023/3/1 9:48
 * 2023-03-01
 */
@Data
@TableName("t_order")
public class Order {

    private Long id;
    private String orderCode;
    private BigDecimal amount;
    private Integer status;

}
