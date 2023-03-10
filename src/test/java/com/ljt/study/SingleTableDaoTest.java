package com.ljt.study;

import com.ljt.study.dao.SingleTableDao;
import com.ljt.study.entity.SingleTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2023-03-01 16:20
 */
@SpringBootTest
class SingleTableDaoTest {

    @Autowired
    private SingleTableDao singleTableDao;


    @Test
    void testInsert() {
        SingleTable st = new SingleTable();
        st.setType("single");
        st.setContent("单表");
        singleTableDao.insert(st);
    }

    @Test
    void testUpdate() {
        SingleTable st = new SingleTable();
        st.setId(2L);
        st.setContent("乐观锁");
        st.setVersion(0);
        singleTableDao.updateById(st);
    }

    @Test
    void testDelete() {
        singleTableDao.deleteById(2L);
    }

    @Test
    void testSelect() {
        singleTableDao.selectList(null).forEach(System.out::println);
    }

}
