package io.zerows.extension.runtime.workflow.uca.toolkit;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.workflow.atom.EngineOn;
import io.zerows.extension.runtime.workflow.atom.configuration.MetaInstance;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.runtime.workflow.uca.modeling.Respect;
import io.zerows.unity.Ux;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ULinkage {

    private final transient MetaInstance metadata;

    public ULinkage(final MetaInstance metadata) {
        this.metadata = metadata;
    }

    private ULinkage(final WRecord record) {
        final WTicket ticket = record.ticket();
        Objects.requireNonNull(ticket);

        // Connect to Workflow Engine
        final EngineOn engine = EngineOn.connect(ticket.getFlowDefinitionKey());
        this.metadata = engine.metadata();
    }

    public static Future<WRecord> readLinkage(final WRecord record) {
        final ULinkage helper = new ULinkage(record);
        return helper.fetchAsync(record, false);
    }

    /*
     * 算法切换，针对当前 record 以及上一个 record 其加载流程会有少许变化，主要在于 Linkage 部分的小范围加载
     * 1）如果 previous = false
     *    则表示当前 record 为新创建的 record，此时需要加载所有的 Linkage，且执行强制加载，做 linkage 的刷新
     * 2）如果 previous = true
     *    则表示当前 record 为上一个 record，此时加载要引入只加载一次的核心算法
     *    - 更新之前，同上边流程
     *    - 更新之后，若 linkage 部分已经有数据则不再加载数据
     *
     * FIX: 附件上传时新旧值一样的问题
     *      https://e.gitee.com/szzw/issues/table?issue=I7HOTO
     */
    private Future<WRecord> fetchAsync(final WRecord record, final boolean previous) {
        // Linkage Extract
        if (this.metadata.linkSkip()) {
            return Ux.future(record);
        }

        // ConcurrentMap
        final ConcurrentMap<String, Future<JsonArray>> futures = new ConcurrentHashMap<>();
        final Set<String> fields = this.metadata.linkFields();
        LOG.Web.info(this.getClass(), "( Fetch ) Linkage Definition Size: {0}", fields.size());
        fields.forEach(field -> {
            final Respect respect = this.metadata.linkRespect(field);
            futures.put(field, respect.fetchAsync(record));
        });
        return FnZero.combineM(futures).compose(dataMap -> {
            dataMap.forEach((field, linkageData) -> {
                if (previous) {
                    final JsonArray stored = record.linkage(field);
                    if (Ut.isNil(stored)) {
                        // 尺寸为 0 时加载，只加载一次
                        record.linkage(field, linkageData);
                    }
                } else {
                    // 强制加载
                    record.linkage(field, linkageData);
                }
            });
            return Ux.future(record);
        });
    }

    public Future<WRecord> syncAsync(final JsonObject params, final WRecord record) {
        /*
         * Old Processing
         */
        final WRecord prev = record.prev();
        if (Objects.nonNull(prev) && prev.data().size() > 0) {
            return this.fetchAsync(prev, true).compose(prevRecord -> {
                record.prev(prevRecord);
                return this.syncAsyncInternal(params, record);
            });
        } else {
            return this.syncAsyncInternal(params, record);
        }
    }

    private Future<WRecord> syncAsyncInternal(final JsonObject params, final WRecord record) {
        /*
         * Linkage Sync based on configuration
         */
        final WTicket ticket = record.ticket();
        if (Objects.isNull(ticket) || this.metadata.linkSkip()) {
            return Ux.future(record);
        }
        final ConcurrentMap<String, Future<JsonArray>> futures = new ConcurrentHashMap<>();
        final Set<String> fields = this.metadata.linkFields();
        LOG.Web.info(this.getClass(), "( Sync ) Linkage Definition Size: {0}", fields.size());
        fields.forEach(field -> {
            /*
             * Data Array extract from `params` based on `field`
             */
            final JsonArray linkageData = Ut.valueJArray(params, field);
            final Respect respect = this.metadata.linkRespect(field);
            futures.put(field, respect.syncAsync(linkageData, params, record));
        });
        return FnZero.combineM(futures).compose(dataMap -> {
            dataMap.forEach(record::linkage);
            return Ux.future(record);
        });
    }
}
