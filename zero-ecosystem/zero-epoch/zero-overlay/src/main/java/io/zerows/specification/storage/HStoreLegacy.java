package io.zerows.specification.storage;

import io.zerows.platform.annotations.meta.One2One;
import io.zerows.platform.enums.EmDS;
import io.zerows.platform.enums.typed.EmType;
import io.zerows.platform.metadata.KDatabase;
import io.zerows.specification.atomic.HContract;
import io.zerows.specification.atomic.HExecutor;
import io.zerows.specification.development.ncloud.HPlot;
import io.zerows.specification.development.program.HResource;
import io.zerows.specification.modeling.norm.HUri;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「存储入口」Store
 * <hr/>
 * 对应抽象存储接口，提供存储基础协议，上层协议会关联到两个核心方向:
 * <pre><code>
 *     1. {@link HResource} 抽象资源存储
 *     2. {@link HPlot} 抽象物理存储
 * </code></pre>
 * 存储中会包含
 * <pre><code>
 *     - identifier()
 *     - version()
 * </code></pre>
 *
 * @author lang : 2023-05-21
 */
public interface HStoreLegacy extends HContract {
    /**
     * 执行器集合，针对不同语义的执行器定义
     *
     * @return {@link ConcurrentMap}
     */
    default ConcurrentMap<String, HExecutor> executor() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 关联存储集合，只包含三种存储类型
     *
     * @return {@link ConcurrentMap}
     */
    default ConcurrentMap<EmType.Store, HStoreLegacy> store() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 「抽象块存储接口」Block Store
     * <hr/>
     * 可搭载不同的块存储器，实现完整的块存储服务，主要服务于分布式文件系统，该接口负责：
     * <pre><code>
     *     1. 分布式块存储
     *     2. 基于底层硬件的块存储
     * </code></pre>
     *
     * @author lang : 2023-05-21
     */
    interface HBlock extends HStoreLegacy {
        /**
         * 块是有序的，所以直接使用 List 类型布局
         *
         * @return {@link List}
         */
        List<HChunk> blocks();

        /**
         * 当前节点的网络标识（分布式专用）
         *
         * @return {@link HUri}
         */
        @One2One
        HUri uri();

        /**
         * 父类
         *
         * @return {@link HBlock}
         */
        HBlock node();

        /**
         * 块可以支持父子级结构
         *
         * @return {@link ConcurrentMap}
         */
        ConcurrentMap<UUID, HBlock> nodes();
    }

    /**
     * 「抽象数据库接口」Database Store
     * <hr/>
     * 可搭载不同的文件读取内容，实现完整的RDBMS服务，用于存储结构搭建，该接口负责：
     * <pre><code>
     *     1. SQL模式的数据库调用
     *     2. NO-SQL模式的数据库调用
     * </code></pre>
     * 直接使用此接口服务，将数据库访问合并到 HDatabase 中
     *
     * @author lang : 2023-05-21
     */
    interface HDatabase extends HStoreLegacy {
        /**
         * 数据库类型
         * <pre><code>
         *     1. MySQL
         *     2. TiDB
         *     3. PgSQL
         * </code></pre>
         *
         * @return {@link EmDS.Database}
         */
        EmDS.Database category();

        /**
         * 数据库实例
         *
         * @return {@link KDatabase}
         */
        KDatabase database();
    }

}
