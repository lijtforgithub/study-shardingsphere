package com.ljt.study;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljt.study.dao.DictDao;
import com.ljt.study.entity.Dict;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2023-03-01 10:07
 */
@SpringBootTest
class DictDaoTest {

    @Autowired
    private DictDao dictDao;


    @Test
    void testInsert() {
        Dict dict = new Dict();
        dict.setTypeKey("open-status");
        dict.setValue("1");
        dict.setValueDesc("开启");
        dictDao.insert(dict);
    }

    @Test
    void testSelect() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getTypeKey, "open-status");
        dictDao.selectList(wrapper).forEach(System.out::println);
    }

}
