package com.ljt.study.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author LiJingTang
 * @date 2022-12-02 9:57
 */
@Slf4j
@MapperScan("com.ljt.study.dao")
@Configuration
class MainConfig {

    @Bean
    ApplicationRunner validateDataSource(DataSource dataSource) {
        return args -> {
            log.info("dataSource: {}", dataSource);
            Connection connection = dataSource.getConnection();
            log.info("初始化数据库连接池 connection: {}", connection);
        };
    }

}
