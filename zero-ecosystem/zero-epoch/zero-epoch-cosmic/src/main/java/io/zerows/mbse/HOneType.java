package io.zerows.mbse;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.epoch.database.jooq.util.JqAnalyzer;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.mbse.metadata.KClass;
import io.zerows.mbse.metadata.KModule;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「类型分析器」
 * Hybrid 建模方式专用的类型加载组件，这种模式下，底层会直接解析 {@link KClass} 类型对象而处理
 * 属性和类型之间的关联关系，并提供别名功能可针对连接之后的模型执行 别名/类型 分析，此处分析的结果和另外两个组件有所区别
 * <pre><code>
 *     {@link HOneJooq} 是负责直接访问器构造
 *     {@link HOneJoin} 是负责连接访问器构造
 * </code></pre>
 * 而当前组件只是负责解析类型，生成的最终数据结构并非组件，而是存储了属性类型的哈希表
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class HOneType implements HOne<ConcurrentMap<String, Class<?>>> {
    @Override
    public ConcurrentMap<String, Class<?>> combine(final KModule module, final KModule connect, final MultiMap headers) {
        // 解析 module 主模型的类型表
        final ConcurrentMap<String, Class<?>> moduleMap = this.typeModule(module);
        final ConcurrentMap<String, Class<?>> typeMap = new ConcurrentHashMap<>(moduleMap);

        if (Objects.nonNull(connect)) {
            /*
             * 解析 connect 连接模型的类型表，存在 JOIN 定义的时候执行此流程
             * 注意 synonym 部分的配置只有在 connect 不为空的时候才会生效，否则不生效
             */
            ConcurrentMap<String, Class<?>> connectMap = this.typeModule(connect);
            final KJoin.Point target = module.getConnect(connect.identifier());
            if (Objects.nonNull(target)) {
                connectMap = this.typeSynonym(connectMap, target);
            }
            typeMap.putAll(connectMap);
        }

        return typeMap;
    }

    /**
     * 同义语义解析器，主要负责 alias 匿名部分的同义语义解析，此处会根据传入的原始类型表和连接点定义两种定义来处理
     * 最终的解析结果
     *
     * @param typedMap 原始类型表
     * @param point    连接点定义
     *
     * @return 解析结果
     */
    private ConcurrentMap<String, Class<?>> typeSynonym(
        final ConcurrentMap<String, Class<?>> typedMap, final KJoin.Point point) {
        final ConcurrentMap<String, Class<?>> typedResult = new ConcurrentHashMap<>();
        if (Objects.isNull(point) || Ut.isNil(point.getSynonym())) {
            // 连接点为 null / synonym 配置为空
            typedResult.putAll(typedMap);
        } else {
            final JsonObject synonym = point.getSynonym();
            typedMap.forEach((field, type) -> {
                if (synonym.containsKey(field)) {
                    // 重命名属性
                    final String targetField = synonym.getString(field);
                    typedResult.put(targetField, type);
                } else {
                    // 保持原始名称
                    typedResult.put(field, type);
                }
            });
        }
        return typedResult;
    }

    /**
     * 重载方法，针对单个模型 {@link KModule} 的所有类型加载，此处的类型加载会包含所有类型
     * <pre><code>
     *     1. 当前属性的所有类型 name = {@link Class}
     *     2. 追加别名之后的所有类型 name = {@link Class}
     * </code></pre>
     * 此处是一个合并结果（扫描之后）。
     *
     * @param module {@link KModule} 模型对象
     *
     * @return {@link ConcurrentMap} 类型的哈希表
     */
    private ConcurrentMap<String, Class<?>> typeModule(final KModule module) {
        final ConcurrentMap<String, Class<?>> typeMap = new ConcurrentHashMap<>();
        final ConcurrentMap<String, Class<?>> moduleMap = this.typeDao(module.getDaoCls());
        final KJoin join = module.getConnect();
        if (Objects.isNull(join)) {
            typeMap.putAll(this.typeSynonym(moduleMap, null));
        } else {
            // KPoint Existing
            typeMap.putAll(this.typeSynonym(moduleMap, join.getSource()));
        }
        return typeMap;
    }

    /**
     * 此方法负责类型加载，但是类型加载中的 class 必须是 {@link io.github.jklingsporn.vertx.jooq.classic.VertxDAO} 类型，实际
     * 所有的实体类的属性名和对应类型是依靠「数据访问器」的类型分析而得的，而不是直接分析实体类而得到的。
     *
     * @param daoCls {@link io.github.jklingsporn.vertx.jooq.classic.VertxDAO} 的子类
     *
     * @return 分析结果，哈希表存储了属性和类型
     */
    private ConcurrentMap<String, Class<?>> typeDao(final Class<?> daoCls) {
        final DBJooq jq = DB.on(daoCls);
        final JqAnalyzer analyzer = jq.analyzer();
        return analyzer.types();
    }
}
