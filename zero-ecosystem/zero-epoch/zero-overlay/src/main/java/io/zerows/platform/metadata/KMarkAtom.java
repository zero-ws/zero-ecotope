package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.modeling.EmAttribute;
import io.zerows.support.base.UtBase;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 模型的属性标记集，保存了当前模型的所有属性标记，用于针对属性执行不同操作。
 * 标记结构在特殊场景中使用，调用时不使用固定方法，而是采用标记位传入的方式来执行相关处理
 * 实现标记的动态扩展，最终的扩展步骤：
 * <pre><code>
 *     1. 直接修改 VAtom.Mark 中的标记信息，添加新的标记位
 *     2. 新版中 {@link Attribute} 和 {@link KMarkAtom} 都不需要更改
 * </code></pre>
 *
 * 特定场景使用标记比直接使用属性的模式要方便，新版充当容器作用，则实现了标记位的动态扩展。
 * 更改时只需重写 {@link EmAttribute.Marker} 枚举类型即可追加新标记
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KMarkAtom {

    /**
     * 是否开启当前 Atom 模型的追踪功能，追踪功能包含优先级：
     * 1. 模型级的追踪功能主要用于定义模型是否产生追踪日志
     * 2. 属性级的追踪功能用于生成变更日志、操作日志等
     * 属性级的追踪依赖模型级的追踪功能是打开的，trackable = true 的模型的属性 track 才生效。
     * 否则所有生成日志的功能在这种场景下都是无效的。
     * 且 trackable 属性是不可以更改的，只能在构造时确定。
     */
    private final boolean trackable;
    private final ConcurrentMap<String, Attribute> markMap = new ConcurrentHashMap<>();

    private KMarkAtom(final Boolean trackable) {
        this.trackable = trackable;
    }

    public static KMarkAtom of(final Boolean trackable) {
        return new KMarkAtom(Optional.ofNullable(trackable).orElse(Boolean.TRUE));
    }

    public boolean trackable() {
        return this.trackable;
    }

    // -------------------- 追加和更改 ---------------------
    public void put(final String name, final Attribute mark) {
        this.markMap.put(name, mark);
    }

    public void put(final String name, final String literal) {
        this.markMap.put(name, Attribute.of(literal));
    }

    public Attribute get(final String name) {
        return this.markMap.getOrDefault(name, Attribute.of());
    }
    // -------------------- 属性提取 ---------------------

    public Set<String> enabled(final EmAttribute.Marker mark) {
        return this.connect(markAttr -> markAttr.value(mark), Boolean.TRUE);
    }

    public Set<String> disabled(final EmAttribute.Marker mark) {
        return this.connect(markAttr -> markAttr.value(mark), Boolean.FALSE);
    }

    private Set<String> connect(final Function<Attribute, Boolean> function, final Boolean defaultV) {
        final Set<String> set = new HashSet<>();
        this.markMap.keySet().forEach(field -> {
            final Attribute tag = this.markMap.get(field);
            if (Objects.nonNull(tag)) {
                final Boolean result = function.apply(tag);
                if (Objects.nonNull(result) && defaultV.booleanValue() == result.booleanValue()) {
                    // Skip all NULL findRunning
                    set.add(field);
                }
            }
        });
        return set;
    }

    /**
     * 旧系统在使用的属性标记，标记当前属性在业务场景下的特殊行为，辅助属性完成完整的属性解析
     * 每种标记都是可配置的模式：
     * <pre><code>
     *     1. 基于数据库配置
     *     2. 基于文件配置
     *     3. 动态配置
     * </code></pre>
     * 属性标记可直接序列化成带逗号的字符串，使用字符串的目的是减少配置数量，提升配置效率
     * 根据构造函数支持的格式：
     * <pre><code>
     *     1. 逗号分隔的字符串（注字面量中索引位置会转换成Boolean）
     *        0,1,2,3,4,5,6,7
     *     2. JsonObject，直接使用属性处理，其他的使用默认值
     *        {
     *            "active": true,
     *            "track": true
     *        }
     *     3. JsonArray，转换成字符串中的集合处理
     * </code></pre>
     * 升级到新版之后，采用属性标记的通用方法，从外部传入标记名，于是 {@link Attribute} 就自然转变成了标记容器。
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public static class Attribute {
        private final ConcurrentMap<String, Boolean> marks = new ConcurrentHashMap<>();

        private Attribute(final List<String> literal) {
            // X,X,X,X,X,X,X,X
            this.marks.putAll(KMarkTool.parse(literal));
        }

        private Attribute(final JsonObject inputJ) {
            this.marks.putAll(KMarkTool.parse(inputJ));
        }

        // -------------------- 静态构造方法 ---------------------
        public static Attribute of() {
            return new Attribute((List<String>) null);
        }

        public static Attribute of(final JsonObject inputJ) {
            return new Attribute(inputJ);
        }

        public static Attribute of(final String literal) {
            final String[] parsed = literal.split(VString.COMMA);
            return new Attribute(List.of(parsed));
        }

        public static Attribute of(final JsonArray arrayA) {
            final List<String> literal = UtBase.toList(arrayA);
            return new Attribute(literal);
        }

        // -------------------- 属性提取 ---------------------
        public boolean value(final EmAttribute.Marker marker) {
            Objects.requireNonNull(marker);
            return KMarkTool.value(marker.name(), this.marks);
        }

        @Override
        public String toString() {
            return KMarkTool.toString(this.getClass().getName(), this.marks);
        }
    }
}
