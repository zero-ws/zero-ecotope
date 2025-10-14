package io.zerows.epoch.assembly;

import io.zerows.epoch.configuration.Inquirer;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-14
 */
@Slf4j
public class InquirerClassAddOn implements Inquirer<Set<Class<?>>> {
    public static final String MESSAGE = "[ ZERO ] ( {} AddOn ) \uD83E\uDDEC Zero 中扫描到 {} 个 AddOn<T> 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> addonSet = clazzes.stream()
            .filter(item -> Ut.isImplement(item, AddOn.class))
            .collect(Collectors.toSet());
        if (!addonSet.isEmpty()) {
            log.info(MESSAGE, addonSet.size(), addonSet.size());
        }
        return addonSet;
    }
}
