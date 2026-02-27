package io.zerows.extension.skeleton.spi;

import java.util.Set;

/**
 * 规则排除处理，排除规则中的路径不执行 Data Scope 行为，默认场景下只要配置了都会执行
 */
public interface ExDefault {

    Set<String> ruleExclude();
}
