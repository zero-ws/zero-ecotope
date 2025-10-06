package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.r2mo.vertx.common.exception.VertxBootException;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.boot.exception._40001Exception500UpClassArgs;
import io.zerows.epoch.boot.exception._40002Exception500UpClassInvalid;
import io.zerows.epoch.boot.internal.FeatureMark;
import io.zerows.epoch.configuration.ZeroBoot;
import io.zerows.management.OZeroStore;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.configuration.HStation;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-05-30
 */
@Slf4j
public class ZeroStation implements HStation {
    /**
     * 针对 Annotation 部分的创建
     */
    private static final ConcurrentMap<String, Annotation> STORE_ANNO = new ConcurrentHashMap<>();
    private static volatile ZeroStation STORE;
    private final ConcurrentMap<FeatureMark, Boolean> features = new ConcurrentHashMap<>();

    private final HBoot boot;

    private ZeroStation() {
        final HSetting setting = ZeroPower.of().compile();
        final JsonObject launcherJ = setting.launcher().options();
        this.boot = ZeroBoot.of(launcherJ);
    }

    public static ZeroStation singleton() {
        if (Objects.isNull(STORE)) {
            synchronized (ZeroStation.class) {
                if (Objects.isNull(STORE)) {
                    STORE = new ZeroStation();
                }
            }
        }
        {
            // session
            STORE.feature(FeatureMark.SESSION, OZeroStore.is(YmlCore.inject.SESSION));
            // init
            STORE.feature(FeatureMark.INIT, OZeroStore.is(YmlCore.init.__KEY));
            // etcd
            final boolean etcd = OZeroStore.is(YmlCore.etcd.__KEY);
            STORE.feature(FeatureMark.ETCD, etcd);
            // gateway
            // 暂时不考虑 API 类型
            STORE.boot().app(etcd ? EmApp.Type.SERVICE : EmApp.Type.APPLICATION);
        }
        return STORE;
    }

    public static ZeroStation singleton(final Class<?> bootingCls, final String... arguments) {
        // 启动检查
        ensure(bootingCls);

        return singleton().bind(bootingCls, arguments);
    }

    /**
     * 「启动规范说明」
     * 1. Zero容器要求输入的clazz必须不能为空，用于后期挂载数据专用
     * 2. 最好在启动类中使用 {@link Up}，否则会导致启动规范的警告，但是不影响启动
     */
    private static void ensure(final Class<?> clazz) {
        // Step 1
        Fn.jvmKo(Objects.isNull(clazz), _40001Exception500UpClassArgs.class);
        // Step 2
        STORE_ANNO.putAll(Anno.get(clazz));
        if (!STORE_ANNO.containsKey(Up.class.getName())) {
            final VertxBootException warning = new _40002Exception500UpClassInvalid(clazz);
            log.warn(warning.getMessage());
        }
    }

    // ------------------- Reference --------------------
    @Override
    public ZeroStation bind(final Class<?> mainClass, final String[] arguments) {
        this.boot.bind(mainClass, arguments);
        return this;
    }

    @Override
    public HBoot boot() {
        return this.boot;
    }

    // ------------------- Feature --------------------

    @Deprecated
    public ZeroStation feature(final FeatureMark mark, final Boolean enabled) {
        this.features.put(mark, enabled);
        return this;
    }

    @Deprecated
    public boolean isEtcd() {
        return false;
    }

    @Deprecated
    public boolean isInit() {
        return false;
    }

    @Deprecated
    public boolean isSession() {
        return this.features
            .getOrDefault(FeatureMark.SESSION, Boolean.FALSE);
    }
}
