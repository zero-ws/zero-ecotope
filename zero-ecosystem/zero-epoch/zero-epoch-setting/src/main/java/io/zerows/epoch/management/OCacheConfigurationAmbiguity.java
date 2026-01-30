package io.zerows.epoch.management;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.epoch.web.MDConnect;
import io.zerows.epoch.web.MDEntity;
import io.zerows.epoch.web.MDId;
import io.zerows.epoch.web.MDMeta;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-05-07
 */
class OCacheConfigurationAmbiguity extends AbstractAmbiguity implements OCacheConfiguration {

    private final ConcurrentMap<String, MDConfiguration> moduleConfig = new ConcurrentHashMap<>();

    OCacheConfigurationAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    /**
     * 特殊查找逻辑和相关方法
     * <pre>
     *     1. 直接从传入的 stored 中提取 {@link MDConnect} 实例
     *        这种情况适用于启动顺序严格处理的模式，如
     *        - Module-01
     *        - Module-CRUD
     *        - Module-02
     *        如果上述启动顺序，Module-02 由于未启动，所以此处还无法查找到 {@link MDConnect} 的情况
     *     2. 为解决上述问题，此处要构造临时的 {@link MDConnect} 实例
     *        从结果上来说，{@link MDEntity} 和 {@link MDConnect} 之间的关系可以是松耦合的状态
     *        - 当 identifier 或 table 表名相同时，{@link MDEntity} 可以关联或绑定相同数据不同地址的 {@link MDConnect} 实例
     *        - 正常模式下 {@link MDConnect} 实例应该是唯一的，但是在模块化环境中，模块之间的启动顺序无法保证，所以这种松耦合形态依旧可以满足
     *     3. 最终形成的网状结构如下
     *        一层数据               二层 - {@link MDEntity}           三层 - {@link MDConnect}             四层 - {@link MDMeta}
     *        identifier-01         01-01                            01-01                               01-01
     *        identifier-01         01-01                            01-02（新）                          01-02（新）
     *        table-01              01-01                            01-01                               01-01
     *        table-02              01-01                            01-02（新）                          01-02（新）
     * </pre>
     * 上述结构中，不论是哪种，对底层而言 01-01 实例和 01-02 实例中的内容都是相同的，只是地址不同而已，这样即使出现“异步丢弃”的问题也不影响最终的结果，一旦容器启动完成后，
     * 依旧可以保证最终配置的结果一致性和正确性，简单说：只要可以访问数据库，最终的结果就是对的！！
     * <p>
     * Fix：上述解决问题的方式参考 {@see Primed} 接口中的说明（后续会包含特殊 Actor 启动）
     *
     * @return MDConnect
     */
    static Set<MDConnect> entireConnect() {
        return CC_SKELETON.values().stream()
            .flatMap(meta -> meta.valueSet().stream())
            .flatMap(meta -> meta.inConnect().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    static MDConnect entireConnect(final String tableOr) {
        return CC_SKELETON.values().stream()
            .flatMap(meta -> meta.valueSet().stream())
            .map(meta -> meta.inConnect(tableOr))
            .filter(Objects::nonNull)
            .findAny().orElse(null);
    }

    @Override
    public MDConfiguration valueGet(final String key) {
        Objects.requireNonNull(key);
        return this.moduleConfig.getOrDefault(key, null);
    }

    @Override
    public JsonObject configurationJ(final String key) {
        final MDConfiguration configuration = this.valueGet(key);
        if (Objects.isNull(configuration)) {
            return new JsonObject();
        }
        final JsonObject configurationJ = configuration.inConfiguration();
        if (Ut.isNil(configurationJ)) {
            return new JsonObject();
        }
        return configurationJ.copy();   // 拷贝副本
    }

    @Override
    public Set<MDConfiguration> valueSet() {
        return new HashSet<>(this.moduleConfig.values());
    }

    @Override
    public OCacheConfiguration add(final MDConfiguration MDConfiguration) {
        Objects.requireNonNull(MDConfiguration);
        final MDId id = MDConfiguration.id();
        this.moduleConfig.put(id.value(), MDConfiguration);
        return this;
    }

    @Override
    public OCacheConfiguration remove(final MDConfiguration MDConfiguration) {
        Objects.requireNonNull(MDConfiguration);
        final MDId id = MDConfiguration.id();
        this.moduleConfig.remove(id.value());
        return this;
    }
}
