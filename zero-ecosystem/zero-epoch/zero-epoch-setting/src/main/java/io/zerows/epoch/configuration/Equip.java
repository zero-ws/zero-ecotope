package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.configuration.HSetting;

/**
 * 「装配器」
 * 读取配置专用，装配器可以用来直接生成 {@link HSetting}
 *
 * @author lang : 2023-05-30
 */
public interface Equip {
    Cc<String, Equip> CC_SKELETON = Cc.openThread();

    static Equip of() {
        return CC_SKELETON.pick(EquipZero::new, EquipZero.class.getName());
    }

    /**
     * 执行装配器初始化
     *
     * @return {@link HSetting}
     */
    HSetting initialize(YmConfiguration configuration);

}
