package com.ljt.study;

import com.ljt.study.dao.MemberDao;
import com.ljt.study.entity.Member;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

/**
 * @author LiJingTang
 * @date 2023-03-01 14:09
 */
@SpringBootTest
class MemberDaoTest {

    @Autowired
    private MemberDao memberDao;


    @Test
    void testInsert() {
        int i = new Random().nextInt(1000);
        HintManager hintManager = HintManager.getInstance();
//        hintManager.addDatabaseShardingValue("member", i);
        hintManager.setDatabaseShardingValue(i);

        Member member = new Member();
        member.setName("商家_" + i);
        memberDao.insert(member);

        hintManager.close();
    }

}
