package io.zerows.extension.runtime.crud.uca.trans;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.epoch.metadata.KTransform;
import io.zerows.epoch.metadata.KTree;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.mbse.metadata.KModule;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class TreeTran implements Tran {
    private transient final boolean isFrom;

    TreeTran(final boolean isFrom) {
        this.isFrom = isFrom;
    }

    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        if (in.canTransform()) {
            return this.tree(in, this.isFrom).apply(data)
                .compose(map -> this.treeAsync(data, in, map));
        } else {
            return Ux.future(data);
        }
    }

    private Function<JsonArray, Future<ConcurrentMap<String, String>>> tree(final IxMod in, final boolean from) {
        return (data) -> {
            final KModule module = in.module();
            final KTransform transform = module.getTransform();
            final KTree tree = transform.getTree();
            if (Objects.isNull(tree)) {
                return Ux.future(new ConcurrentHashMap<>());
            }
            /* Pick Parent Database Value
             * out = key
             * in = code
             * */
            final JsonArray values;
            final Kv<String, String> keyValue;
            if (from) {
                /*
                 * From is importing, code will be the condition
                 * code = key
                 */
                values = Ut.toJArray(Ut.valueSetString(data, tree.getField()));
                keyValue = Kv.create(tree.getIn(), tree.getOut());

            } else {
                // key = code
                values = Ut.toJArray(Ut.valueSetArray(data, tree.getField()));
                keyValue = Kv.create(tree.getOut(), tree.getIn());
            }
            return this.treeAsync(in, tree, values, keyValue).compose(map -> {
                /*
                 * Because key could not be modified, it means that the
                 * code = key should keep `map` priority
                 */
                final ConcurrentMap<String, String> mapInput = this.tree(data, keyValue);
                mapInput.forEach((field, key) -> {
                    if (!map.containsKey(field)) {
                        map.put(field, key);
                    }
                });
                return Ux.future(map);
            });
        };
    }

    private Future<ConcurrentMap<String, String>> treeAsync(final IxMod in, final KTree tree,
                                                            final JsonArray values,
                                                            final Kv<String, String> keyValue) {
        final JsonObject criteria = tree.region(in.parameters());
        final String keyField = keyValue.key();
        criteria.put(keyField + ",i", values);
        criteria.put(VString.EMPTY, Boolean.TRUE);
        LOG.Web.info(this.getClass(), "Tree Transform Condition: {0}", criteria.encode());
        final UxJooq jooq = IxPin.jooq(in);
        return jooq.fetchJAsync(criteria).compose(source -> Ux.future(this.tree(source, keyValue)));
    }

    private ConcurrentMap<String, String> tree(final JsonArray source,
                                               final Kv<String, String> keyValue) {
        final ConcurrentMap<String, String> data = new ConcurrentHashMap<>();
        Ut.itJArray(source).forEach(record -> {
            final String fromValue = record.getString(keyValue.key());
            final String toValue = record.getString(keyValue.value());
            if (Objects.nonNull(fromValue) && Objects.nonNull(toValue)) {
                data.put(fromValue, toValue);
            }
        });
        return data;
    }

    private Future<JsonArray> treeAsync(final JsonArray data, final IxMod in, final ConcurrentMap<String, String> map) {
        if (!map.isEmpty()) {
            final KModule module = in.module();
            final KTree tree = module.getTransform().getTree();
            final String field = tree.getField();
            Ut.itJArray(data).forEach(record -> {
                if (record.containsKey(field)) {
                    final String value = record.getString(field);
                    final String to = map.get(value);
                    /*
                     * When you do mass updating on records, sometimes the `parentId` is null
                     * The transform rules is as following:
                     * 1) When parentId is null, skip processing.
                     * 2) When parentId is not null, transform here
                     *
                     * ( Mass Update ) Skip Null is true, it means that when to = null, skip putting
                     * ( Importing ) Skip Null is false ( Default ), when the convert is null, put null into
                     */
                    record.put(field, to);
                }
            });
        }
        return Ux.future(data);
    }
}
