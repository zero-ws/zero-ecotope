package io.zerows.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一接口，替换原始的 UxPool 以及 Rapid，用于处理不同业务场景之下的共享数据池操作，内置 Bridge 桥接功能。
 * <pre>
 *     1. 初始化时选择使用哪种实现 -> {@link SharedClient} / {@link CachedClient}
 *        - {@link SharedClient}
 *          - {@link MemoAtMapLocal} 本地同步缓存
 *          - {@link MemoAtMapCluster} 集群异步缓存
 *        - {@link CachedClient}
 *          - 通过 SPI 抓取实现类
 *     2. 提供统一的缓存操作接口 / 不同场景之下调用的 Client 会有所区别
 *     3. 开发调用
 *        - HMM 可直接调用，模式正确即可
 *        - {@link SharedClient} 使用注入模式，直接根据 vertx 是否启用了集群 isClustered 来判断使用哪种
 *        - {@link CachedClient} 则在注入过程中使用 SPI 机制加载对应的实现（会多一个参数执行选择）
 * </pre>
 * 上层可定义新的 Mem 接口用于存储不同数据结构，其中包括，存储在什么位置取决于 {@link HMM} 初始化参数
 * <pre>
 *     - {@link JsonObject} 基本键值对
 *     - {@link JsonArray}  字典列表
 *     - {@link UserAt}     登录用户缓存信息（安全对接）
 * </pre>
 * 最终架构如：
 * <pre>
 *     - {@link JsonObject} --> HMM --> MemoAt --> Shared / Local
 *     - {@link JsonArray}  -->                    Shared / Cluster
 *     - {@link UserAt}     -->                    Cached / Redis
 *                                                 Cached / Caffeine
 *                                                 Cached / Ehcache
 *     - MemoAt 捆绑了 Name / Duration / Size 核心参数
 * </pre>
 */
@Slf4j
@SuppressWarnings("all")
public class HMM {

    private static final Cc<String, HMM> CC_UX_POOL = Cc.open();
    private static final String HMM_DEFAULT = HMM.class.getName();
    private final String name;
    private final SharedClient sharedClient;
    private final CachedClient cachedClient;

    private HMM(final String name) {
        this.name = name;
        this.sharedClient = SharedAddOn.of().createInstance(name);
        this.cachedClient = CachedAddOn.of().createInstance(name);
    }

    public static HMM of(final String name) {
        final String nameHMM = Ut.isNil(name) ? HMM_DEFAULT : name;
        return CC_UX_POOL.pick(() -> new HMM(nameHMM), nameHMM);
    }

    public String name() {
        return this.name;
    }
}
