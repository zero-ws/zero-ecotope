package io.zerows.platform.management;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.cc.Cc;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.app.HApp;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
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
@Slf4j
class StoreSettingAmbiguity extends AbstractAmbiguity implements StoreSetting {

    private static final Cc<String, HSetting> CC_SETTING = Cc.open();
    private static final Cc<String, Object> CC_NETWORK = Cc.open();
    private static final ConcurrentMap<Class<?>, String> CC_BIND = new ConcurrentHashMap<>();

    protected StoreSettingAmbiguity(final HBundle owner) {
        super(owner);
    }

    private ConcurrentMap<String, HSetting> refSetting() {
        return CC_SETTING.get();
    }

    private ConcurrentMap<String, Object> refNetwork() {
        return CC_NETWORK.get();
    }

    @Override
    public Set<String> keys() {
        return this.refSetting().keySet();
    }

    @Override
    public HSetting valueGet(final String key) {
        return this.refSetting().getOrDefault(key, null);
    }

    @Override
    public StoreSetting add(final HSetting setting) {
        if (Objects.nonNull(setting) && StrUtil.isNotBlank(setting.id())) {
            log.info("[ ZERO ] 当前配置的 ID 为 {} 已成功添加！", setting.id());
            this.refSetting().put(setting.id(), setting);
        }
        return this;
    }

    @Override
    public StoreSetting remove(final HSetting setting) {
        if (Objects.nonNull(setting) && StrUtil.isNotBlank(setting.id())) {
            log.info("[ ZERO ] 当前配置的 ID 为 {} 已成功移除！", setting.id());
            this.refSetting().remove(setting.id());
        }
        return this;
    }

    @Override
    public StoreSetting bind(final Class<?> bootCls, final String id) {
        if (Objects.nonNull(bootCls) && StrUtil.isNotBlank(id)) {
            log.info("[ ZERO ] \t \uD83C\uDF7B ID = {} 被 Boot 类 {} 成功绑定！", id, bootCls.getName());
            CC_BIND.put(bootCls, id);
        }
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public <T> T getNetwork(final HSetting setting) {
        if (Objects.isNull(setting) || Objects.isNull(setting.id())) {
            return null;
        }
        return (T) this.refNetwork().get(setting.id());
    }

    @Override
    public <T> StoreSetting add(final HSetting setting, final T network) {
        if (Objects.isNull(setting) || Objects.isNull(network)) {
            return this;
        }
        this.refNetwork().put(setting.id(), network);
        log.info("[ ZERO ] \t \uD83C\uDF7B ID = {} 中添加 {}", setting.id(), network.getClass().getName());
        return this;
    }

    @Override
    public HSetting getBy(final Class<?> bootCls) {
        final String settingId = CC_BIND.getOrDefault(bootCls, null);
        if (Objects.isNull(settingId)) {
            return null;
        }
        return this.refSetting().getOrDefault(settingId, null);
    }
}
