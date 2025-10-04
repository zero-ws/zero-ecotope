package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDMeta;
import io.zerows.epoch.metadata.Mirror;
import io.zerows.epoch.metadata.Mojo;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-10
 */
class OnenessConnect implements Oneness<MDConnect> {
    /**
     * 主键计算器
     * <pre><code>
     *      1. 如果 pojoFile 未配置，直接返回 key 主键值
     *      2. 如果当前表是关系表，则 key 的主键应该是 null 的，关系表直接通过 {@link MDMeta} 判断
     * </code></pre>
     *
     * @param connect 连接对象 {@link MDConnect}
     *
     * @return 主键
     */
    @Override
    public String keyPrimary(final MDConnect connect) {
        final MDMeta meta = connect.meta();
        Objects.requireNonNull(meta);
        if (!meta.isEntity()) {
            // 关系表，前缀 R_
            return null;
        }


        // 实体表，非 R_ 前缀
        final String pojoFile = connect.getPojoFile();
        if (Ut.isNil(pojoFile)) {
            return connect.getKey();
        } else {
            // 带有 pojoFile 映射的主键
            final Mojo mojo = Mirror.create(connect.getClass())
                .mount(pojoFile)
                .type(connect.getDao()).mojo();
            return mojo.getIn(connect.getKey());
        }
    }

    @Override
    public Set<String> keyUnique(final MDConnect connect) {
        // 关系表和实体表在计算此处时无障碍
        final JsonArray unique = connect.getUnique();
        if (Ut.isNil(unique)) {
            return Set.of();
        }


        final String pojoFile = connect.getPojoFile();
        if (Ut.isNil(pojoFile)) {
            return Ut.toSet(unique);
        } else {
            final Set<String> uniqueSet = new HashSet<>();
            final Mojo mojo = Mirror.create(this.getClass())
                .mount(pojoFile)
                .type(connect.getDao()).mojo();
            Ut.itJArray(unique, String.class, (field, index) -> {
                final String converted = mojo.getIn(field);
                if (Objects.isNull(converted)) {
                    uniqueSet.add(field);
                } else {
                    uniqueSet.add(converted);
                }
            });
            return uniqueSet;
        }
    }
}
