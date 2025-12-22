package io.zerows.platform.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.exception._40103Exception500ConnectAmbient;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.atomic.HBelong;
import io.zerows.specification.cloud.HFrontier;
import io.zerows.specification.cloud.HGalaxy;
import io.zerows.specification.cloud.HSpace;
import io.zerows.specification.vital.HOI;
import io.zerows.support.base.UtBase;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
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
 *     1. 一个环境上下文：{@link Context}
 *     2. 一个环境运行时：{@link Runtime}
 *     3. 一个核心配置对象
 *     4. 应用模式，对应：{@link EmApp.Mode}
 * </pre>
 *
 * @author lang : 2023-06-05
 */
public class KAmbient implements HAmbient {
    private final ConcurrentMap<String, JsonObject> configuration = new ConcurrentHashMap<>();
    private final Context context = new Context();
    private final Runtime vector = new Runtime();
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

    /**
     * @author lang : 2023-06-07
     */
    static class Runtime {
        private static final ConcurrentMap<String, String> VECTOR = new ConcurrentHashMap<>();

        /**
         * 替换原始的 HES / HET / HOI 专用
         * <pre><code>
         *     构造向量表
         *     name
         *     ns           = cacheKey
         *     id
         *     appKey       = cacheKey
         *     tenantId     = cacheKey
         *     sigma        = cacheKey
         * </code></pre>
         *
         * @param ark  HArk 应用配置容器
         * @param mode EmApp.Mode 应用模式
         */
        void registry(final HArk ark, final EmApp.Mode mode) {
            // 提取缓存键和应用程序引用
            final String key = UtBase.keyApp(ark);
            final HApp app = ark.app();

            // 1. 基础规范：name / ns
            final String ns = app.ns();
            VECTOR.put(ns, key);
            final String name = app.name();
            VECTOR.put(name, key);

            // 2. 扩展规范：code / appKey / id
            final String code = app.option(VName.CODE);
            Optional.ofNullable(code).ifPresent(each -> VECTOR.put(each, key));
            final String appId = app.option(VName.APP_ID);
            Optional.ofNullable(appId).ifPresent(each -> VECTOR.put(each, key));
            final String appKey = app.option(VName.APP_KEY);
            Optional.ofNullable(appKey).ifPresent(each -> VECTOR.put(each, key));

            // 3. 选择规范：sigma / tenantId
            if (EmApp.Mode.CUBE == mode) {
                // 单租户 / 单应用，tenantId / sigma 可表示缓存键（唯一的情况）
                final HOI hoi = ark.owner();
                Optional.ofNullable(hoi).map(HBelong::owner).ifPresent(each -> VECTOR.put(each, key));
                final String sigma = app.option(VName.SIGMA);
                Optional.ofNullable(sigma).ifPresent(each -> VECTOR.put(each, key));
            }
        }

        String keyFind(final String key) {
            return VECTOR.getOrDefault(key, null);
        }
    }

    /**
     * @author lang : 2023-06-06
     */
    static class Context {
        private static final Cc<String, HArk> CC_ARK = Cc.open();

        Context() {
        }

        HArk running() {
            final Collection<HArk> arks = CC_ARK.values();
            if (VValue.ONE == arks.size()) {
                return arks.iterator().next();
            }
            throw new _40103Exception500ConnectAmbient();
        }

        HArk running(final String cacheKey) {
            return Optional.ofNullable(cacheKey)
                .map(CC_ARK::get)
                .orElse(null);
        }

        ConcurrentMap<String, HArk> app() {
            return CC_ARK.get();
        }

        EmApp.Mode registry(final HArk ark) {
            final String cacheKey = UtBase.keyApp(ark);
            CC_ARK.put(cacheKey, ark);
            // 注册结束后编织应用的上下文
            // 环境中应用程序超过 1 个时才执行其他判断
            final ConcurrentMap<String, HArk> store = CC_ARK.get();
            final HBelong belong = ark.owner();
            EmApp.Mode mode = EmApp.Mode.CUBE;
            if (VValue.ONE < store.size()) {
                if (belong instanceof HFrontier) {
                    mode = EmApp.Mode.FRONTIER;        // Frontier
                } else if (belong instanceof HGalaxy) {
                    mode = EmApp.Mode.GALAXY;          // Galaxy
                } else if (belong instanceof HSpace) {
                    mode = EmApp.Mode.SPACE;           // Space
                }
            }
            return mode;
        }
    }
}
