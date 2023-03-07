package com.ljt.study.sharding.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.spring.boot.registry.AbstractAlgorithmProvidedBeanRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author LiJingTang
 * @date 2023-03-06 16:40
 */
@Slf4j
public class ShardingAlgorithmRefreshProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final String PROPS_MAP = "propsMap";
    @Autowired
    private ApplicationContext applicationContext;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private Set<String> beanNames = new HashSet<>();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanDefinitionRegistry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void refresh(YamlShardingRuleConfiguration yamlConfig) {
        applicationContext.getBeansOfType(AbstractAlgorithmProvidedBeanRegistry.class).values().forEach(registry -> {
            Reflector reflector = new DefaultReflectorFactory().findForClass(registry.getClass());
            Invoker invoker = reflector.getGetInvoker(PROPS_MAP);
            try {
                Map<String, Properties> propsMap = (Map<String, Properties>) invoker.invoke(registry, null);
                beanNames.addAll(propsMap.keySet());

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            registry.postProcessBeanDefinitionRegistry(this.beanDefinitionRegistry);
        });

        log.info("已注册Algorithm：{}", beanNames);

        Stream.concat(Stream.concat(yamlConfig.getKeyGenerators().keySet().stream(), yamlConfig.getShardingAlgorithms().keySet().stream()),
                yamlConfig.getAuditors().keySet().stream()).forEach(beanName -> {
                    if (!beanNames.contains(beanName)) {
                        log.info("新增Algorithm：{} = {}", beanName, applicationContext.getBean(beanName));
                    }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
