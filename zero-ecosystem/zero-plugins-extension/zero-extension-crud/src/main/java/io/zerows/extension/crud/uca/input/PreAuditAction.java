package io.zerows.extension.crud.uca.input;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

/**
 * @author lang : 2023-08-04
 */
abstract class PreAuditAction implements Pre {
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
                this.log().info("{} Audit By 条件 -> \"{}\" = {}", IxConstant.K_PREFIX, by, userId);
                data.put(by, userId);
            }
            final String at = config.getString(KName.AT);
            if (Ut.isNotNil(at)) {
                this.log().info("{} Audit At 字段 -> {}", IxConstant.K_PREFIX, at);
                data.put(at, Instant.now());
            }
        }
    }

    private Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
