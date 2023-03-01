package com.ljt.study;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljt.study.dao.OperateLogDao;
import com.ljt.study.entity.OperateLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author LiJingTang
 * @date 2022-12-02 10:47
 */
@SpringBootTest
class OperateLogDaoTest {

    @Autowired
    private OperateLogDao operateLogDao;

    @Test
    void testInsert() {
        IntStream.rangeClosed(1, 10).mapToObj(i -> {
            OperateLog log = new OperateLog();
            log.setUserId((long) i);
            log.setOperate("单个新增_" + i);
            log.setContent("分库");
            return log;
        }).forEach(log -> operateLogDao.insert(log));
    }

    @Test
    void testInsertList() {
        List<OperateLog> list = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            OperateLog log = new OperateLog();
            log.setUserId((long) i);
            log.setOperate("批量新增_" + i);
            log.setContent("分库");
            return log;
        }).collect(Collectors.toList());

        operateLogDao.insertList(list);
    }

    @Test
    void testSelectOfEq() {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperateLog::getUserId, 1L);
        operateLogDao.selectList(wrapper).forEach(System.out::println);
    }

    @Test
    void testSelectOfIn() {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OperateLog::getUserId, 1L, 2L, 3L);
        operateLogDao.selectList(wrapper).forEach(System.out::println);
    }

    @Test
    void testSelectOfRange() {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(OperateLog::getUserId, 3L);
        operateLogDao.selectList(wrapper).stream().sorted(Comparator.comparingLong(OperateLog::getUserId)).forEach(System.out::println);
    }

    @Test
    void testSelectAll() {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(OperateLog::getUserId, 3L).last("LIMIT 5");
        operateLogDao.selectList(wrapper).stream().sorted(Comparator.comparingLong(OperateLog::getUserId)).forEach(System.out::println);
    }

}
