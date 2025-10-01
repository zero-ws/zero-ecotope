package io.zerows.epoch.spi.boot;

import io.zerows.specification.configuration.HSetting;

/**
 * 「装配器」
 * 读取配置专用，装配器可以用来直接生成 {@link HSetting}
 *
 * @author lang : 2023-05-30
 */
public interface HEquip {
    /**
     * 执行装配器初始化
     *
     * @return {@link HSetting}
     */
    HSetting initialize();
}
