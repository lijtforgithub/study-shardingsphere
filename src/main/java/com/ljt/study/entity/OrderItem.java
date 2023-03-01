package com.ljt.study.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LiJingTang
 * @date 2023-03-01 09:50
 */
@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private String goodsName;
    private Integer num;
    private BigDecimal price;
    private BigDecimal amount;

}
