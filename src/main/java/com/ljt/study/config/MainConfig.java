package com.ljt.study.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.ThreadLocalRandom;

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

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            private final ThreadLocalRandom random = ThreadLocalRandom.current();

            @Override
            public Expression getTenantId() {
                return new LongValue(random.nextInt(100));
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                return !"single_table".equalsIgnoreCase(tableName);
            }
        }));
        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
        return interceptor;
    }

}
