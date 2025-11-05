package io.zerows.extension.crud.uca.next;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.spi.SPI;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.destine.Conflate;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.extension.crud.uca.desk.IxMod;
import io.zerows.extension.crud.uca.desk.IxReply;
import io.zerows.mbse.metadata.KModule;
import io.zerows.platform.enums.EmDS;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class NtJData implements Co<JsonObject, JsonObject, JsonObject, JsonObject> {

    private transient final IxMod in;

    NtJData(final IxMod in) {
        this.in = in;
    }

    @Override
    public Future<JsonObject> next(final JsonObject input, final JsonObject active) {
        if (this.in.canJoin()) {
            final Conflate<JsonObject, JsonObject> conflate =
                Conflate.ofJObject(this.in.connect(), false);

            /*
             * 输入：
             * 1. 先执行数据合并
             * 2. 再执行引用处理
             */
            final JsonObject dataSt = conflate.treat(active, input, this.in.connectId());

            this.referenceInput(dataSt);

            LOG.Web.info(this.getClass(), "Data In: {0}", dataSt.encode());
            return Ux.future(dataSt);
        } else {
            // There is no joined module join current
            return Ux.future(active.copy());
        }
    }

    /**
     * 特殊步骤说明，这种模式下，连接点不可以为空 {@link KJoin.Point}
     * <pre><code>
     *     父主表 / 父从表模式统一处理
     *     1. 首先是源不可以为空（源为空不可做任何连接），必定会存在连接点信息
     *        外层已经使用 {@link IxMod#canJoin()} 做过检查，所以此处的 {@link KJoin} 是一定不会为空的
     *        Action：这种模式下，直接移除 primary key 的主键定义 key
     *     2. 其次提取连接数据相关信息执行输入转换，此处分两种
     *        1）如果数据中包含了连接点的信息，连接点一般位于 active 模型
     *           - 父主表则是 A.key join B.aKey，此处移除 A.key
     *           - 父从表则是 B.aKey join A.key，此处移除 B.aKey
     *        2）直接提取 keyJoin 中的信息，填充到 primary key
     *        Action：第二个步骤属于重塑主键，用于将主键的数据值填充到环境中
     * </code></pre>
     */
    private void referenceInput(final JsonObject dataSt) {
        final KModule module = this.in.module();
        final KJoin join = module.getConnect();
        Objects.requireNonNull(join);
        final String keyField = module.getField().getKey();
        final KJoin.Point source = join.getSource();

        // 默认异常行为
        this.referenceSource(dataSt);

        final String keyJoin = source.getKeyJoin();
        if (Ut.isNil(keyJoin) || keyJoin.equals(keyField)) {
            // keyJoin = keyField
            dataSt.remove(keyJoin);
            return;
        }

        // keyJoin != keyField
        final String valueJoin = dataSt.getString(keyJoin);
        if (Ut.isNotNil(valueJoin)) {
            dataSt.put(keyField, valueJoin);
        }
    }

    @Override
    public Future<JsonObject> ok(final JsonObject active, final JsonObject standBy) {
        final WebState status = IxReply.getStatus(standBy, false);
        final HttpResponseStatus statusValue = status.value();
        if (SPI.V_STATUS.ok204().state() == statusValue.code()) {
            return Ux.future(active);
        }
        if (this.in.canJoin()) {

            /*
             * 输出：
             * 1. 先执行引用处理
             * 2. 再执行数据合并
             */
            final Conflate<JsonObject, JsonObject> conflate =
                Conflate.ofJObject(this.in.connect(), true);

            this.referenceOut(standBy);

            final JsonObject dataSt = conflate.treat(active, standBy, this.in.connectId());
            LOG.Web.info(this.getClass(), "Data Out: {0}", dataSt.encode());
            return Ux.future(dataSt);
        } else {
            // There is no joined module join current
            return Ux.future(active.copy());
        }
    }

    /**
     * 由于此处方向是 standBy -> 覆盖 -> activeJ
     * 所以要执行特殊运算，避免相关行为
     * <pre><code>
     *     1. 要将 standByJ 的值移除才可以保证主键不被覆盖
     *     2. 父主表模式
     *        - aKey 存在（无视）
     *        - 被 Join 的表的主键会直接移除
     *        由于主表的 key 存在，所以 standByJ 可直接覆盖
     *        父从表模式
     *        - 主要是防止主键 primary key 直接被覆盖
     *        由于主表的 key 不存在，所以 standByJ 不可以覆盖
     *     3. 两种模式输出的时候信息不一致
     *        - 父主表：主表输出为 standBy 从表主键（父表有第二属性 aKey）
     *        - 父从表：主表输出为主表逐渐，standBy 的逐渐忽略（从表有第二属性 aKey）
     * </code></pre>
     */
    private void referenceOut(final JsonObject standByJ) {
        // 默认异常行为
        this.referenceSource(standByJ);

        final KModule module = this.in.module();
        final KJoin join = module.getConnect();
        if (EmDS.Connect.PARENT_STANDBY == join.refer()) {
            final KModule connect = this.in.connected();
            Objects.requireNonNull(connect);
            final String keyField = connect.getField().getKey();
            if (Ut.isNotNil(keyField)) {
                standByJ.remove(keyField);
            }
        }
    }

    private void referenceSource(final JsonObject inputJ) {
        final KModule module = this.in.module();
        final KJoin join = module.getConnect();
        Objects.requireNonNull(join);
        final String keyField = module.getField().getKey();
        final KJoin.Point source = join.getSource();
        if (Objects.isNull(source)) {
            // 未定义，使用默认值
            inputJ.remove(keyField);
        }
    }
}
