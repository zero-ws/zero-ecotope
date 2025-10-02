package io.zerows.epoch.component.normalize;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.configuration.module.MDConnect;
import io.zerows.epoch.configuration.module.MDMeta;
import io.zerows.epoch.mem.module.OCacheDao;
import io.zerows.epoch.program.Ut;

import java.util.Objects;

/**
 * 延迟使用，构造时候才使用此操作，主要针对旧版数据表片段处理
 * <pre><code>
 *     dao - xxx
 *     pojoFile - xxx
 *     unique - xxx
 *     key - xxx
 * </code></pre>
 *
 * @author lang : 2024-05-10
 */
class ReplacerConnect implements Replacer<MDConnect> {
    /**
     * 旧版格式和新版格式的转换
     * <pre><code>
     *     1. 旧版格式
     *     - dao: io.zerows.extension.mbse.modulat.domain.tables.daos.BAuthorityDao   # B_AUTHORITY
     *       key: key
     *       unique:
     *        - code
     *        - blockId
     *     2. 新版格式
     *     - dao: X_SOURCE   # X_SOURCE
     *       unique:
     *       - appId
     * </code></pre>
     *
     * @param configJ 核心格式
     *
     * @return {@link MDConnect}
     */
    @Override
    public MDConnect build(final JsonObject configJ) {
        final String daoOrTable = Ut.valueString(configJ, KName.DAO);
        // 提取 MDMeta 底层存储
        final MDMeta meta = OCacheDao.entireMeta(daoOrTable);
        final MDConnect connect = Fn.jvmOr(() -> Ut.deserialize(configJ, MDConnect.class));
        if (Objects.isNull(meta)) {
            final Class<?> daoCls = Ut.clazz(daoOrTable, null);
            /*
             * 旧版流程，依旧走序列化处理，这种模式下的限制
             * 1）daoOrTable 必须是 Java 类全名，并且要求类全名必须是可加载的不可加载的类全名直接跳过
             * 2）表名不可以为空，如果表名为空也直接跳过，这种模式下表名需执行反向计算
             */
            if (Objects.isNull(connect) || Objects.isNull(daoCls)) {
                this.logger().warn("Invalid Dao = {} could not be parsed and ignored.", daoOrTable);
                return null;
            }
            connect.setDao(daoCls);     // 后置绑定，解决序列化问题


            final MDMeta metaDeep = OCacheDao.entireMeta(daoCls);
            if (Objects.isNull(metaDeep)) {
                this.logger().warn("Dao = {} could not be parsed and ignored.", daoOrTable);
                return null;
            }


            return connect.build(metaDeep);
        } else {
            /* 新版流程，直接走非序列化，这种模式无限制，绑定合法即可 */
            return connect.build(meta);
        }
    }
}
