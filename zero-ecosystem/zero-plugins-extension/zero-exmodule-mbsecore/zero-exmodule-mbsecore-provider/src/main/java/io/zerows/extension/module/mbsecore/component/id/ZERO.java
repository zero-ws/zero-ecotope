package io.zerows.extension.module.mbsecore.component.id;

import io.r2mo.function.Fn;
import io.zerows.extension.module.mbsecore.domain.tables.pojos.MField;
import io.zerows.extension.module.mbsecore.domain.tables.pojos.MJoin;
import io.zerows.extension.module.mbsecore.exception._80514Exception409PrimaryKeyResult;
import io.zerows.extension.module.mbsecore.exception._80515Exception417PrimaryKeySize;
import io.zerows.extension.module.mbsecore.metadata.Model;
import io.zerows.extension.module.mbsecore.metadata.Schema;
import io.zerows.platform.enums.modeling.EmKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {
    ConcurrentMap<EmKey.Mode, AoId> POOL_ID = new ConcurrentHashMap<EmKey.Mode, AoId>() {
        {
            this.put(EmKey.Mode.DIRECT, new DirectId());
            this.put(EmKey.Mode.COLLECTION, new CollectionId());
            this.put(EmKey.Mode.JOIN_KEY, new JoinKeyId());
            this.put(EmKey.Mode.JOIN_MULTI, new JoinMultiId());
            this.put(EmKey.Mode.JOIN_COLLECTION, new JoinCollectionId());
        }
    };
}

class
Ensurer {
    /*
     * Join 模式的定义
     */
    static Set<Boolean> join(final Class<?> clazz,
                             final Model model) {
        final Set<MJoin> joins = model.dbJoins();
        /*
         * 第一检查条件：一对多的 Join 模式，keyMap的尺寸必须大于 1
         */
        Fn.jvmKo(1 >= joins.size(), _80514Exception409PrimaryKeyResult.class,
            /* ARG1：出现该异常的目标类名 */ clazz.getName(),
            /* ARG2：当前实体的主键信息 */ AoId.keyInfo(joins));

        /*
         * 第二检查条件：keyMap的尺寸 和 schema 的数量一致
         * Join Multi 必须是：
         * key -> Entity1 / key
         * key -> Entity2 / ciKey / key
         */
        Fn.jvmKo(joins.size() < model.schema().size(), _80515Exception417PrimaryKeySize.class,
            /* ARG1：实际的主键数量 */ model.schema().size(), // 没个 Schema 一个主键
            /* ARG2：期望的主键数量 */ String.valueOf(joins.size()));

        final Set<Boolean> valid = new HashSet<>();
        joins.forEach((entry) -> {
            final String identifier = entry.getEntity();
            final String keyField = entry.getEntityKey();

            final Schema schema = model.schema(identifier);
            final List<MField> keys = schema.getPrimaryKeys();

            /*
             * 第三检查条件：没个 Schema 的主键只能是单字段主键
             */
            Fn.jvmKo(1 < keys.size(), _80515Exception417PrimaryKeySize.class,
                /* ARG1：实际的主键数量 */ keys.size(),
                /* ARG2：期望的主键数量 */ String.valueOf(1));

            final MField field = schema.getField(keyField);
            valid.add(field.getIsPrimary());
        });
        /* 返回的主键的数量 */
        return valid;
    }
}