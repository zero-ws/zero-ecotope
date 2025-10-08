package io.zerows.platform;

import io.zerows.platform.enums.EmApp;
import io.zerows.specification.app.HApp;
import io.zerows.specification.configuration.HSetting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 直接放在内存中存储，作为运行时设置对待，多应用、多服务运行时，此类主管所有的 {@link HSetting} 实例化对象存储，
 * 对以下实例而言，它的数量设计
 * <pre>
 *     1. 应用 {@link HApp} 中的 {@link HSetting} 实例：1:1
 *     2. 根据 {@link EmApp.Type} 类型区分
 *        - {@link EmApp.Type#APPLICATION} 应用类型：1:1
 *        - {@link EmApp.Type#SERVICE} 服务类型：1:1
 *        - {@link EmApp.Type#GATEWAY} 网关类型：1:1
 *     3. 此处的 CC_SETTING 的 key 设计为 String 类型，表示应用唯一标识符
 *        - 静态应用 -> vertx.application.name
 *        - 动态应用 -> X_APP 的 id 值
 * </pre>
 *
 * @author lang : 2025-10-08
 */
public final class SettingManager {

    private static final ConcurrentMap<String, HSetting> CC_SETTING = new ConcurrentHashMap<>();

    private static final SettingManager INSTANCE = new SettingManager();

    private SettingManager() {
    }

    public static SettingManager of() {
        return INSTANCE;
    }

    public SettingManager put(final String name, final HSetting setting) {
        CC_SETTING.put(name, setting);
        return this;
    }

    public HSetting get(final String name) {
        return CC_SETTING.getOrDefault(name, null);
    }
}
