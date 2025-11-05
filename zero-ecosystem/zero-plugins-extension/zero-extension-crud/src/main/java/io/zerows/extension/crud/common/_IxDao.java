package io.zerows.extension.crud.common;

import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.core.json.JsonObject;
import io.zerows.component.environment.DevEnv;
import io.zerows.cortex.extension.HExtension;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.mbse.metadata.KModule;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.crud.common.Ix.LOG;

;

/*
 * Dao class initialization
 * plugin/crud/module/ folder singleton
 */
class IxDao {
    private static final MultiKeyMap<KModule> MODULE_MAP = new MultiKeyMap<>();

    /**
     * 入口配置，此配置中会出现重写，但重写过程中不会更新原始信息，只是在原始信息的基础上进行重写，重写一般位于启动模块，
     * 重写规则如下
     * <pre><code>
     *     zero-launcher-configuration -> MODULE_MAP（高优先级）
     *     其他 MDConfiguration -> MODULE_MAP（低优先级）
     * </code></pre>
     */
    static void initWithOverwrite() {
        final MDConfiguration entryConfiguration = HExtension.getOrCreate(IxConstant.ENTRY_CONFIGURATION);
        /*
         * Boot: Secondary founding to pick up default configuration
         */
        final Set<HExtension> boots = HExtension.initialize();
        final Set<String> logId = new TreeSet<>();
        final ConcurrentMap<String, String> logMap = new ConcurrentHashMap<>();
        boots.forEach(boot -> boot.module().forEach((moduleKey, json) -> {
            // 构造 KModule
            final MDEntity entity = entryConfiguration.inEntity(moduleKey);
            final JsonObject moduleData = json.copy();
            if (Objects.nonNull(entity)) {
                final JsonObject moduleJ = entity.inModule();
                moduleData.mergeIn(moduleJ, true);
            }
            final KModule config = Ut.deserialize(moduleData, KModule.class);


            // 默认值
            final String identifier = IxInitializer.configure(config, moduleKey);
            IxConfiguration.addUrs(config.getName());
            MODULE_MAP.put(identifier, config, config.getName());


            if (DevEnv.devDaoBind()) {
                logId.add(identifier);
                logMap.put(identifier, Ut.fromMessage(IxMsg.INIT_INFO, identifier, config.getName()));
            }
        }));
        logId.forEach(identifier -> LOG.Init.info(IxDao.class, logMap.get(identifier)));
        LOG.Init.info(IxDao.class, "IxDao Finished ! Size = {0}, Uris = {0}",
            MODULE_MAP.values().size(), IxConfiguration.getUris().size());
    }

    static KModule get(final String actor) {
        final KModule config = MODULE_MAP.getOr(actor);
        if (Objects.isNull(config)) {
            LOG.Rest.warn(IxDao.class, "Actor: identifier = `{}` configuration is missing!", actor);
            return null;
        } else {
            LOG.Rest.info(IxDao.class, "Actor: identifier = `{0}`", actor);
            return config;
        }
    }
}
