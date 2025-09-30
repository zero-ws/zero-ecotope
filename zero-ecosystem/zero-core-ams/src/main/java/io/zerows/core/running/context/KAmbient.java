package io.zerows.core.running.context;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.em.EmApp;
import io.zerows.core.exception.web._60050Exception501NotSupport;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;

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
 *
 * @author lang : 2023-06-05
 */
public class KAmbient implements HAmbient {
    private final ConcurrentMap<String, JsonObject> configuration = new ConcurrentHashMap<>();
    private final _KAmbientContext context = new _KAmbientContext();
    private final _KAmbientRuntime vector = new _KAmbientRuntime();
    private EmApp.Mode mode;

    private KAmbient() {
        this.mode = EmApp.Mode.CUBE;
    }

    public static HAmbient of() {
        return new KAmbient();
    }

    @Override
    public EmApp.Mode mode() {
        return this.mode;
    }

    @Override
    public HArk running(final String key) {
        Objects.requireNonNull(key);
        final String keyFind = this.vector.keyFind(key);
        return this.context.running(keyFind);
    }

    @Override
    public HArk running() {
        if (EmApp.Mode.CUBE != this.mode) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        return this.context.running();
    }

    @Override
    public JsonObject extension(final String name) {
        return this.configuration.getOrDefault(name, new JsonObject());
    }

    @Override
    public ConcurrentMap<String, HArk> app() {
        return this.context.app();
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
        // 1. 注册应用
        this.mode = this.context.registry(ark);
        // 2. 运行时绑定
        this.vector.registry(ark, this.mode);
        return this;
    }
}
