package io.zerows.epoch.corpus.cloud;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.cloud.HName;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.atomic.HCommand;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.development.ncloud.HNovae;
import io.zerows.specification.development.ncloud.HStarter;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 「启动组件」
 * 启动组件专用配置
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AeonStarter implements HStarter, Serializable {
    private static final ConcurrentMap<String, Class<?>> STORE_DEFINE = new ConcurrentHashMap<>() {
        {
            // 生命周期管理：启动、运行、停止专用组件
            // component
            this.put(HName.ON, HConfig.HOn.class);                      // on
            this.put(HName.OFF, HConfig.HOff.class);                    // off
            this.put(HName.RUN, HConfig.HRun.class);                    // run

            // 和GitHub集成专用结构
            // alive
            this.put(HName.ALIVE_NOVAE, HNovae.class);          // novae
            this.put(HName.ALIVE_NOVA, HNovae.HOne.class);            // nova
            this.put(HName.ALIVE_NEBULA, HNovae.HNebula.class);        // nebula
        }
    };
    /*
     * 内部哈希表 map，存储结构如：
     * -- 接口名 = 实现类
     * 最终实例会链接到 CCT_EVENT 的组件缓存池，缓存键维度
     * -- 1）线程名
     * -- 2）类型名
     * 最终都是 Async 实例
     */
    private final ConcurrentMap<Class<?>, Class<?>> store = new ConcurrentHashMap<>();

    private AeonStarter(final JsonObject configJ) {
        // component + alive
        final JsonObject componentJ = Ut.valueJObject(configJ, HName.COMPONENT);
        final JsonObject aliveJ = Ut.valueJObject(configJ, HName.ALIVE);

        final JsonObject sourceJ = componentJ.copy().mergeIn(aliveJ, true);
        STORE_DEFINE.forEach((name, interfaceCls) -> {
            final Class<?> instanceCls = Ut.valueCI(sourceJ, name, interfaceCls);
            if (Objects.nonNull(instanceCls)) {
                // 接口 = 实现类
                this.store.put(interfaceCls, instanceCls);
            }
        });
        LogCloud.LOG.Aeon.info(this.getClass(),
            "Aeon system detect ( size = {0} with keys = {1} ) components defined.",
            String.valueOf(this.store.size()),
            Ut.fromJoin(this.store.keySet().stream().map(Class::getName).collect(Collectors.toSet())));
    }

    public static HStarter configure(final JsonObject configJ) {
        final JsonObject configuration = Ut.valueJObject(configJ);
        return CStoreCloud.CC_BOOT.pick(() -> new AeonStarter(configuration), configuration.hashCode());
    }

    @Override
    public <C> C starter(final Class<?> interfaceCls, final Vertx vertx) {
        return this.starter(interfaceCls, vertx, null);
    }

    @SuppressWarnings("all")
    @Override
    public <C> C starter(final Class<?> interfaceCls, final Vertx vertx, final Class<?> defaultCls) {
        Objects.requireNonNull(interfaceCls);
        final Class<?> instanceCls = this.store.getOrDefault(interfaceCls, defaultCls);
        if (Objects.isNull(instanceCls)) {
            return null;
        }
        final HCommand.Async event = CStoreCloud.CCT_EVENT.pick(() -> {
            final HCommand.Async instance = Ut.instance(instanceCls);
            instance.bind(vertx);
            return instance;
        }, instanceCls.getName());
        LogCloud.LOG.Aeon.info(getClass(), "Pick instance class {0} of {1} from component cached/pool.",
            instanceCls, interfaceCls);
        return (C) event;
    }
}
