package io.zerows.component.destine;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.Memory;
import io.zerows.epoch.corpus.metadata.specification.KJoin;
import io.zerows.epoch.corpus.metadata.specification.KPoint;

/**
 * 「Hyphen翻译」练字符 -，此处表示「配置连接点」
 * 新方法，执行新版的链接点筛选专用 {@link KPoint}，目前的 zero-crud 版本只支持双表模式，所以 父从表和父主表只能选择一种模式进行关联（二选一），后期可拓展成多表链接同时支持两种模式，但目前版本支持两种模式即可以解决核心问题了。
 * 此接口主要用于构造 {@link KPoint}，替换原始的 connection 方法。
 * <pre><code>
 *     1. 父从表模式：reference 定义
 *     2. 父主表模式：target 定义
 * </code></pre>
 * 此处组件主要处理事项是根据 {@link KJoin} 中的定义，分不同的模式直接解析出对应的连接点，这些连接点可以帮助后续的 JOIN 操作处理和执行，并实现连接点的分模式、分类解析流程。
 *
 * @author lang : 2023-07-27
 */
@SuppressWarnings("unchecked")
public interface Hymn<T> {

    @Memory(Hymn.class)
    Cc<String, Hymn> CCT_HYMN = Cc.openThread();

    static Hymn<String> ofString(final KJoin join) {
        return CCT_HYMN.pick(() -> new HymnString(join), HymnString.class.getName());
    }

    static Hymn<JsonObject> ofJObject(final KJoin join) {
        return CCT_HYMN.pick(() -> new HymnJObject(join), HymnJObject.class.getName());
    }

    static Hymn<JsonArray> ofJArray(final KJoin join) {
        return CCT_HYMN.pick(() -> new HymnJArray(join), HymnJArray.class.getName());
    }

    /**
     * 此处 Input 数据类型最终会影响解析的连接点相关信息
     *
     * @param input 输入数据
     *
     * @return {@link KPoint} 连接点
     */
    KPoint pointer(T input);
}