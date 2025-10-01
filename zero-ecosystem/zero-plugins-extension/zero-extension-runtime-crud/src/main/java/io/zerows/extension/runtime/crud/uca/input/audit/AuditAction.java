package io.zerows.extension.runtime.crud.uca.input.audit;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.util.Ix;

import java.time.Instant;
import java.util.Objects;

/**
 * @author lang : 2023-08-04
 */
abstract class AuditAction implements Pre {
    /**
     * 主要针对 auditor 的四个核心字段
     * <pre><code>
     *     1. createdBy / createdAt
     *     2. updatedBy / updatedAt
     * </code></pre>
     * 该API方法设置的时候，是分组设置的，即添加（created）、更新（updated）分别设置，其中 `by` 会设置成当前用户ID，`at` 会设置成当前时间戳
     *
     * @param data   被设置的对象数据
     * @param config 被设置的配置数据
     * @param userId 被设置的用户ID
     */
    void setAuditor(final JsonObject data, final JsonObject config, final String userId) {
        if (Objects.nonNull(config) && Ut.isNotNil(userId)) {
            /* User By */
            final String by = config.getString(KName.BY);
            if (Ut.isNotNil(by)) {
                /* Audit Process */
                Ix.LOG.Dao.info(this.getClass(), "( Audit ) By -> \"{0}\" = {1}", by, userId);
                data.put(by, userId);
            }
            final String at = config.getString(KName.AT);
            if (Ut.isNotNil(at)) {
                Ix.LOG.Dao.info(this.getClass(), "( Audit ) At Field -> {0}", at);
                data.put(at, Instant.now());
            }
        }
    }
}
