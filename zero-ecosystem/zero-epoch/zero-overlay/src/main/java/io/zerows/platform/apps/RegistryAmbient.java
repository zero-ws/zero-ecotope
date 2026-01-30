package io.zerows.platform.apps;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.platform.management.StoreArk;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 标准化应用容器池，用于在运行环境中存储应用程序基本信息，按照租户本身存在的内容执行梯度化处理：
 * <pre><code>
 *     1. app / tenant / owner
 *     2. app / tenant /
 *     3. app {@link HArk}
 * </code></pre>
 * 核心容器环境，表示单个容器环境，此容器环境中会包含
 * <pre>
 *     1. 一个环境上下文：{@link StoreArk}
 *     2. 一个核心配置对象
 *     3. 应用模式，对应：{@link EmApp.Mode}
 * </pre>
 *
 * @author lang : 2023-06-05
 */
@Slf4j
class RegistryAmbient implements HAmbient {
    private final ConcurrentMap<String, JsonObject> configuration = new ConcurrentHashMap<>();
    private final StoreArk context = StoreArk.of();
    private EmApp.Mode mode;

    private RegistryAmbient(final EmApp.Mode mode) {
        this.mode = mode;
    }

    static HAmbient of(final HConfig config) {
        if (Objects.isNull(config)) {
            return new RegistryAmbient(EmApp.Mode.CUBE);
        }
        final String modeStr = config.options(VName.MODE);
        final EmApp.Mode mode = UtBase.toEnum(modeStr, EmApp.Mode.class, EmApp.Mode.CUBE);
        return new RegistryAmbient(mode);
    }

    /**
     * 读取当前应用程序环境的基础模式
     * <pre>
     *    1. {@link EmApp.Mode#CUBE}            单租户 / 单应用
     *    2. {@link EmApp.Mode#SPACE}           单租户 / 多应用模式
     *    3. {@link EmApp.Mode#GALAXY}          多租户 / 多应用模式
     *    4. {@link EmApp.Mode#FRONTIER}        边界星云模型
     * </pre>
     *
     * @return {@link EmApp.Mode}
     */
    @Override
    public EmApp.Mode mode() {
        return this.mode;
    }

    @Override
    public HAmbient mode(final EmApp.Mode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public HArk running(final String key) {
        Objects.requireNonNull(key);
        return this.context.valueGet(key);
    }

    /**
     * 必须是单应用模式下才能调用此方法
     *
     * @return {@link HArk}
     */
    @Override
    public HArk running() {
        if (EmApp.Mode.CUBE != this.mode) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        return this.context.value();
    }

    @Override
    public boolean isReady() {
        final ConcurrentMap<String, HArk> apps = this.context.valueMap();
        if (Objects.isNull(apps) || apps.isEmpty()) {
            return false;
        }
        return apps.values().stream()
            .map(HArk::app)
            .anyMatch(Objects::nonNull);
    }

    @Override
    public JsonObject extension(final String name) {
        return this.configuration.getOrDefault(name, new JsonObject());
    }

    @Override
    public ConcurrentMap<String, HArk> app() {
        return this.context.valueMap();
    }

    /**
     * 此方法实现采取同步，主要原因是注册过程中很容易会出现多个模块同时注册的情况，在这种模式下使用同步不会出现线程安全问题，
     * 但是会出现阻塞的情况，因此在注册过程中不要执行过多的操作，仅仅是注册即可，而内部采用了 {@link ConcurrentMap} 的模式
     * 保证了注册的线程安全性。
     */
    @Override
    public synchronized HAmbient registry(final String extension, final JsonObject configuration) {
        this.configuration.put(extension, configuration);
        return this;
    }

    @Override
    public synchronized HAmbient registry(final HArk ark) {
        // 规范检查
        this.context.add(ark);
        return this;
    }
}
