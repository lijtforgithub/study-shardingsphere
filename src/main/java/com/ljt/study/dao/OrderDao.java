package com.ljt.study.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljt.study.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * @author LiJingTang
 * @date 2023-03-01 11:06
 */
public interface OrderDao extends BaseMapper<Order> {

    List<Map<String, Object>> selectOrder(Long orderId);

}
