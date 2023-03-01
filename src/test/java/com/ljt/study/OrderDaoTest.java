package com.ljt.study;

import com.ljt.study.dao.OrderDao;
import com.ljt.study.dao.OrderItemDao;
import com.ljt.study.entity.Order;
import com.ljt.study.entity.OrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author LiJingTang
 * @date 2023-03-01 11:11
 */
@SpringBootTest
class OrderDaoTest {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;


    @Test
    void testInsert() {
        OrderItem item = new OrderItem();
        item.setGoodsName("手机");
        item.setNum(1);
        item.setPrice(BigDecimal.valueOf(1899));
        item.setAmount(BigDecimal.valueOf(item.getNum()).multiply(item.getPrice()));

        Order order = new Order();
        order.setOrderCode(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ROOT));
        order.setStatus(0);
        order.setAmount(item.getAmount());
        orderDao.insert(order);

        item.setOrderId(order.getId());
        orderItemDao.insert(item);
    }

    @Test
    void testSelect() {
        orderDao.selectOrder(null).forEach(System.out::println);
    }

}
