package com.ljt.study.sharding.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.*;

/**
 * @author LiJingTang
 * @date 2023-03-01 13:55
 */
@Slf4j
public class MyHintShardingAlgorithm implements HintShardingAlgorithm<Long> {

    @Override
    public Collection<String> doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
        List<Integer> values = new ArrayList<>(shardingValue.getValues());
        String mod = "" + values.get(0) % 2;

        Optional<String> optional = availableTargetNames.stream().filter(name -> name.toString().endsWith(mod)).findFirst();
        if (optional.isPresent()) {
            return Collections.singleton(optional.get());
        }
        return Collections.emptyList();
    }

    @Override
    public Properties getProps() {
        log.info("getProps");
        return null;
    }

    @Override
    public void init(Properties props) {
        props.putIfAbsent("test", "xxoo");
        log.info("init --- {}", props);
    }

}
