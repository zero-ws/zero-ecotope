package io.zerows.component.destine;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.Log;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.metadata.KPoint;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 针对原始系统中的 dataIn / dataOut / dataCond 执行强替换，每一种替换使用一个子类来处理，这里是抽象类，不可直接使用，需要使用子类来实现，和 {@link Hymn} 不同的点在于：
 * <pre><code>
 *     1. {@link Hymn} 主要负责连接点的解析，解析的结果为一个 {@link KPoint}，主要作用点在于 {@link KJoin} 的连接定义部分。
 *     2. {@link Conflate} 则负责数据的处理，作用点在于 {@link KPoint} 部分的数据处理。
 * </code></pre>
 * 通常在解析流程的拓扑主要如下：
 * <pre><code>
 *     IxMod         ---->
 *                   KModule    ( module )     当前模块
 *                   KModule    ( connect )    连接模块，依赖 {@link Hymn}
 *                   -- 连接模块的计算依赖 {@link Hymn} 来实现，根据三种不同输入可直接解析对应连接点，只要连接点存在
 *                      那么连接模块就一定存在，connect 存在时才可能调用 canJoin() 模式执行当前环境中的构造部分，防
 *                      止无连接点的模块执行。
 *
 *                   ---->
 *                   直接从 KModule ( module ) 中提取 connect 节点，根据 identifier 构造连接点，此处的连接点作为
 *                   数据处理的入口，构造的 {@link Conflate} 可作用于当前连接点处理来执行不同的数据处理流程，所以此处
 *                   IxMod 中保留 pointer 的提取，这种提取在于已完全解析好了 connect 作为预处理条件，若 connect 解
 *                   析失败，此处的构造就直接处理失败不会执行，否则就会执行两种模式的的数据叠加态
 *                   - active + input
 *                   - active
 *                   上述两种叠加态主要用于带有连接部分的处理
 *
 *     新版追加       ---->
 *                   connect 在解析过程中执行二选一的处理模式
 *                   - 父主表模式，根据 target 动态提取被连接的表结构，然后执行连接完成之后的数据处理（动态）
 *                   - 父从表模式，根据 reference 动态提取连接的主表结构（静态）
 *
 *                   * 现阶段版本两种模式只能二选一，不支持同时有父表和子表的情况，后续版本也不推荐这样的做法，主要原因在
 *                   于若一个表同时存在父表、子表就意味着模型已经演化成了三表 JOIN 的模式，这种模式在数据库中是不推荐的
 *                   （出于性能考虑），所以此处的 connect 计算最终固化成二选一。
 * </code></pre>
 *
 * @author lang : 2023-07-30
 */
public abstract class ConflateBase<I, O> implements Conflate<I, O> {

    protected final transient KJoin joinRef;

    protected ConflateBase(final KJoin joinRef) {
        this.joinRef = joinRef;
    }

    /**
     * 当前模块的基础配置，这个配置为 {@link KPoint}，此配置不能为空
     *
     * @return {@link KPoint} 配置
     */
    protected KPoint source() {
        final KPoint source = this.joinRef.getSource();
        Objects.requireNonNull(source);
        return source;
    }

    protected String sourceKey() {
        // joinRef 中提取
        if (Objects.isNull(this.joinRef)) {
            return KName.KEY;
        }
        // 连接点提取
        final KPoint source = this.joinRef.getSource();
        if (Objects.isNull(source)) {
            return KName.KEY;
        }
        // 根据 keyJoin 计算
        return Ut.isNil(source.getKeyJoin()) ? KName.KEY : source.getKeyJoin();
    }

    /**
     * 「动态连接」
     * 当前模块的连接目标配置，次配置同样为 {@link KPoint}结构，有可能为空，由于是动态连接模式，所以
     * 此处的连接点可以为空，根据传入的 identifier 模型标识符动态构造连接点，执行流程
     * <pre><code>
     *     1. 根据当前模块 module 的 getConnect() 计算连接点
     *     2. 获取连接点配置，{@link KPoint}
     * </code></pre>
     *
     * @param identifier 连接点的标识符
     *
     * @return {@link KPoint} 配置
     */
    protected KPoint target(final String identifier) {
        final Hymn<String> hymn = Hymn.ofString(this.joinRef);
        final KPoint point = hymn.pointer(identifier);
        Log.info(this.getClass(), "Point = {0}, To = {1}", point, identifier);
        return point;
    }

    protected String targetKey(final String identifier) {
        final KPoint target = this.target(identifier);
        return Objects.isNull(target) ? KName.KEY : target.getKeyJoin();
    }

    // ---------------------- 数据处理专用 -----------------------

    protected void procEach(final JsonArray active, final JsonArray assist, final String identifier,
                            final BiConsumer<JsonObject, JsonObject> consumerFn) {
        // 抽取 key 属性信息
        final String sourceKey = this.sourceKey();
        Objects.requireNonNull(sourceKey);
        final String targetKey = this.targetKey(identifier);
        Ut.itJArray(active).forEach(sourceJ -> {
            final Object value = sourceJ.getValue(sourceKey);
            if (Objects.isNull(targetKey) || Objects.isNull(value)) {
                // targetKey == null || get == null
                consumerFn.accept(sourceJ, null);
            } else {
                // targetKey != null && get != null
                final JsonObject found = Ut.elementFind(assist, targetKey, value);
                consumerFn.accept(sourceJ, found);
            }
        });
    }

    // source.key / target.keyJoin --> qr
    protected JsonObject procQr(final JsonObject active, final String identifier) {
        final String keySource = this.sourceKey();
        final KPoint target = this.target(identifier);
        final JsonObject dataJoin = new JsonObject();
        if (Objects.nonNull(target)) {
            // 若连接点存在，则执行连接点部分的数据
            String joinedValue = active.getString(keySource);
            if (Ut.isNil(joinedValue)) {
                joinedValue = active.getString(target.getKeyJoin());
            }
            // 最终合并的Qr数据相关信息
            if (Ut.isNotNil(joinedValue)) {
                dataJoin.put(target.getKeyJoin(), joinedValue);
            }
        }
        return dataJoin;
    }

    // source.key -> target.keyJoin
    protected JsonObject procInput(final JsonObject active, final String identifier) {
        final String keySource = this.sourceKey();
        final KPoint target = this.target(identifier);
        final JsonObject dataJoin = new JsonObject();
        if (Objects.nonNull(target)) {
            // 若连接点存在，则执行连接点部分的数据
            final String joinedValue = active.getString(keySource);
            if (Ut.isNotNil(joinedValue)) {
                dataJoin.put(target.getKeyJoin(), joinedValue);
            }
        }
        return dataJoin;
    }

    // target.keyJoin -> source.key
    protected JsonObject procOutput(final JsonObject active, final String identifier) {
        final String keySource = this.sourceKey();
        final KPoint target = this.target(identifier);
        final JsonObject dataJoin = new JsonObject();
        if (Objects.nonNull(target)) {
            // 若连接点存在，则执行连接点部分的数据
            final String joinedValue = active.getString(target.getKeyJoin());
            if (Ut.isNotNil(joinedValue)) {
                dataJoin.put(keySource, joinedValue);
            }
        }
        return dataJoin;
    }
}
