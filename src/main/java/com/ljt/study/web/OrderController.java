package com.ljt.study.web;

import com.ljt.study.dao.OrderDao;
import com.ljt.study.sharding.algorithm.ShardingRuleConfigRefreshProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author LiJingTang
 * @date 2023-03-03 16:57
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ShardingRuleConfigRefreshProcessor shardingRuleConfigRefreshProcessor;

    @GetMapping
    public List<Map<String, Object>> get() {
        return orderDao.selectOrder(null);
    }

    @SneakyThrows
    @GetMapping("/refresh")
    public String refresh() {
        shardingRuleConfigRefreshProcessor.refresh();

        return "OK";
    }

}
