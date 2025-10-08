package io.zerows.platform;

import io.zerows.specification.configuration.HSetting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 直接放在内存中存储，作为运行时设置对待，多应用、多服务运行时
 *
 * @author lang : 2025-10-08
 */
public final class SETTING {

    private static final ConcurrentMap<String, HSetting> CC_SETTING = new ConcurrentHashMap<>();

    private static final SETTING INSTANCE = new SETTING();

    private SETTING() {
    }

    public static SETTING of() {
        return INSTANCE;
    }

    public SETTING put(final String name, final HSetting setting) {
        CC_SETTING.put(name, setting);
        return this;
    }

    public HSetting get(final String name) {
        return CC_SETTING.getOrDefault(name, null);
    }
}
