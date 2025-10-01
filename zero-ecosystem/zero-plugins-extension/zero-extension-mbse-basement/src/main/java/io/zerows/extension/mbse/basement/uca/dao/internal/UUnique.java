package io.zerows.extension.mbse.basement.uca.dao.internal;

import io.zerows.epoch.common.uca.qr.Criteria;
import io.zerows.extension.mbse.basement.atom.data.DataEvent;
import io.zerows.specification.modeling.HRecord;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

/**
 * 工具类
 * 1. 只支持单记录结果
 * 2. 支持 SELECT 返回结果
 * 3. 连接查询引擎做细粒度查询
 * 只返回唯一数据集：
 * {
 * * field1: xx
 * * field2: xx
 * }
 */
public class UUnique extends AbstractUtil<UUnique> {

    private UUnique() {
    }

    public static UUnique create() {
        return new UUnique();
    }

    public <ID> HRecord fetchById(final ID id) {
        LOG.SQL.info(this.getLogger(), "执行方法：UUnique.fetchById, {0}", id);
        // Input
        final DataEvent input = this.idInput(id);
        // Output
        return this.output(input, this.jooq::fetchById, false);
    }

    public HRecord fetchOne(final Criteria criteria) {
        LOG.SQL.info(this.getLogger(), "执行方法：UUnique.fetchOne");
        // Input
        final DataEvent input = this.irCond(criteria);
        // Output
        return this.output(input, this.jooq::fetchOne, false);
    }

    // ----------------------- Private ----------------------
    /*
     * 起点：仅生成绑定了 ids 的 DataEvent
     */
    private <ID> DataEvent idInput(final ID ids) {
        return this.event().keys(ids);
    }
}
