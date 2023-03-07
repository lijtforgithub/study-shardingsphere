package com.ljt.study.sharding.algorithm;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.config.database.impl.DataSourceProvidedDatabaseConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.infra.config.rule.scope.GlobalRuleConfiguration;
import org.apache.shardingsphere.infra.instance.metadata.InstanceMetaData;
import org.apache.shardingsphere.infra.instance.metadata.InstanceMetaDataBuilderFactory;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderFactory;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderParameter;
import org.apache.shardingsphere.sharding.algorithm.config.AlgorithmProvidedShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.apache.shardingsphere.sharding.spi.ShardingAlgorithm;
import org.apache.shardingsphere.sharding.spi.ShardingAuditAlgorithm;
import org.apache.shardingsphere.sharding.spring.boot.rule.YamlShardingRuleSpringBootConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.swapper.YamlShardingRuleAlgorithmProviderConfigurationSwapper;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.apache.shardingsphere.spring.boot.prop.SpringBootPropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ShardingRuleSpringBootConfiguration
 * ShardingSphereAutoConfiguration
 *
 * @author LiJingTang
 * @date 2023-03-06 18:44
 */
@Slf4j
public class ShardingRuleConfigRefreshProcessor {

    private static final String CONTEXT_MANAGER = "contextManager";
    private static final String DATA_SOURCE_MAP = "dataSourceMap";
    private static final String DATA_BASE_NAME = "databaseName";
    private final YamlShardingRuleAlgorithmProviderConfigurationSwapper swapper = new YamlShardingRuleAlgorithmProviderConfigurationSwapper();

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ShardingSphereDataSource shardingSphereDataSource;
    @Autowired
    private ShardingSphereAutoConfiguration shardingSphereAutoConfiguration;
    @Autowired
    private SpringBootPropertiesConfiguration props;
    @Autowired
    private YamlShardingRuleSpringBootConfiguration yamlConfig;
    @Autowired
    private ConfigurationPropertiesBindingPostProcessor configurationPropertiesBindingPostProcessor;
    @Autowired
    private ShardingAlgorithmRefreshProcessor algorithmRefresh;

    @SneakyThrows
    public void refresh() {
        log.info(JSON.toJSONString(yamlConfig.getSharding()));

        String[] beanNames = applicationContext.getBeanNamesForType(YamlShardingRuleSpringBootConfiguration.class);
        if (beanNames.length == 0) {
            return;
        }

        String beanName = beanNames[0];
        YamlShardingRuleSpringBootConfiguration bean = applicationContext.getBean(beanName, YamlShardingRuleSpringBootConfiguration.class);
        YamlShardingRuleConfiguration shardingConfig = bean.getSharding();
        rebindConfig(beanName, bean, shardingConfig);

        log.info(JSON.toJSONString(shardingConfig));

        algorithmRefresh.refresh(shardingConfig);

        resetContextManage(shardingConfig);
    }

    private void rebindConfig(String beanName, YamlShardingRuleSpringBootConfiguration bean, YamlShardingRuleConfiguration shardingConfig) {
        shardingConfig.getTables().clear();
        shardingConfig.getAutoTables().clear();
        shardingConfig.getBindingTables().clear();
        shardingConfig.getBroadcastTables().clear();
        configurationPropertiesBindingPostProcessor.postProcessBeforeInitialization(bean, beanName);
    }

    private void resetContextManage(YamlShardingRuleConfiguration shardingConfig) throws IllegalAccessException, InvocationTargetException, SQLException {
        Reflector reflector = new DefaultReflectorFactory().findForClass(ShardingSphereDataSource.class);
        Invoker getInvoker = reflector.getGetInvoker(CONTEXT_MANAGER);
        ContextManager originContextManage = (ContextManager) getInvoker.invoke(shardingSphereDataSource, null);

        Invoker setInvoker = reflector.getSetInvoker(CONTEXT_MANAGER);

        AlgorithmProvidedShardingRuleConfiguration ruleConfig = newRuleConfiguration(shardingConfig);

        Reflector reflectorAutoConfig = new DefaultReflectorFactory().findForClass(ShardingSphereAutoConfiguration.class);
        ContextManager contextManager = createContextManager(getDataBaseName(reflectorAutoConfig),
                shardingSphereAutoConfiguration.modeConfiguration(), getDataSourceMap(reflectorAutoConfig),
                Collections.singleton(ruleConfig), props.getProps());

        setInvoker.invoke(shardingSphereDataSource, new Object[]{contextManager});

        originContextManage.close();
    }

    private AlgorithmProvidedShardingRuleConfiguration newRuleConfiguration(YamlShardingRuleConfiguration yamlConfig) {
        AlgorithmProvidedShardingRuleConfiguration ruleConfig = swapper.swapToObject(yamlConfig);
        ruleConfig.setShardingAlgorithms(applicationContext.getBeansOfType(ShardingAlgorithm.class));
        ruleConfig.setKeyGenerators(applicationContext.getBeansOfType(KeyGenerateAlgorithm.class));
        ruleConfig.setAuditors(applicationContext.getBeansOfType(ShardingAuditAlgorithm.class));
        return ruleConfig;
    }

    private Map<String, DataSource> getDataSourceMap(Reflector reflector) throws InvocationTargetException, IllegalAccessException {
        Invoker invoker = reflector.getGetInvoker(DATA_SOURCE_MAP);
        return (Map<String, DataSource>) invoker.invoke(shardingSphereAutoConfiguration, null);
    }

    private String getDataBaseName(Reflector reflector) throws InvocationTargetException, IllegalAccessException {
        Invoker invoker = reflector.getGetInvoker(DATA_BASE_NAME);
        return (String) invoker.invoke(shardingSphereAutoConfiguration, null);
    }

    private ContextManager createContextManager(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                                final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        InstanceMetaData instanceMetaData = InstanceMetaDataBuilderFactory.create("JDBC", -1);
        Collection<RuleConfiguration> globalRuleConfigs = ruleConfigs.stream().filter(GlobalRuleConfiguration.class::isInstance).collect(Collectors.toList());
        Collection<RuleConfiguration> databaseRuleConfigs = new LinkedList<>(ruleConfigs);
        databaseRuleConfigs.removeAll(globalRuleConfigs);
        ContextManagerBuilderParameter parameter = new ContextManagerBuilderParameter(modeConfig, Collections.singletonMap(databaseName,
                new DataSourceProvidedDatabaseConfiguration(dataSourceMap, databaseRuleConfigs)), globalRuleConfigs, props, Collections.emptyList(), instanceMetaData, false);
        return ContextManagerBuilderFactory.getInstance(modeConfig).build(parameter);
    }

}
