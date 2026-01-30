package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.jigsaw.Equip;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.platform.management.StoreSetting;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 *
 * @author lang : 2025-10-06
 */
@Slf4j
class ZeroPowerBridge implements ZeroPower {
    private static final Cc<String, ZeroPower.Source> CC_SOURCE = Cc.openThread();

    @Override
    public HSetting compile(final Class<?> bootCls) {
        final ZeroPower.Source source = CC_SOURCE.pick(ZeroSource::new, ZeroSource.class.getName());

        final YmConfiguration configuration = source.load();

        final HSetting setting = Equip.of().initialize(configuration);

        this.manageSetting(bootCls, setting);

        return setting;
    }

    private void manageSetting(final Class<?> bootCls, final HSetting setting) {
        Objects.requireNonNull(setting, "[ ZERO ] 配置装配器返回的配置不能为空！");
        if (Ut.isNil(setting.id())) {
            log.warn("[ ZERO ] 当前配置的 ID 为空，不会被 SettingManager 管理！");
        } else {
            StoreSetting.of().add(setting).bind(bootCls, setting.id());
        }
    }
}
