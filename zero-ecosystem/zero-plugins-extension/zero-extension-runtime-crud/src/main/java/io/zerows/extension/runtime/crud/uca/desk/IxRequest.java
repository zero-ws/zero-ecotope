package io.zerows.extension.runtime.crud.uca.desk;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.metadata.KPoint;
import io.zerows.epoch.metadata.KView;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.platform.enums.EmDS;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * Here are the new data structure for input data
 * 1) Envelop will convert to IxOpt to panel
 * 2) Here are different module extractor in internal code logical for connect
 * 3) This component will provide the feature that are similar with IxNext ( Old Version )
 * 4) Provide the mapping from `active` to `standBy`
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class IxRequest {
    private final static String LOGGER_MOD = "active=\u001b[1;36m{0}\u001b[m, standby=\u001b[1;95m{1}\u001b[m, api={2}, view={3}";
    private final transient ApiSpec apiSpecification;
    // IxMod Calculation
    private transient IxMod active;
    private transient IxMod standBy;
    // Input Parameters
    private transient KView view;
    private transient String key;
    private transient String actor;
    private transient JsonObject bodyJ;
    private transient JsonArray bodyA;

    private IxRequest(final ApiSpec spec) {
        this.apiSpecification = spec;
    }

    public static IxRequest create(final ApiSpec spec) {
        return new IxRequest(spec);
    }

    /**
     * 直接从请求中解读关联的 module 的信息。解析优先级按如下顺序：
     * <pre><code>
     *     1. 根据请求中 API 的定义：{@link ApiSpec}。
     *        - {@link ApiSpec#BODY_JSON}: Body格式是 {@link JsonObject} 内容的请求。
     *        - {@link ApiSpec#BODY_ARRAY}：Body格式是 {@link JsonArray} 内容的请求。
     *        - {@link ApiSpec#BODY_WITH_KEY}：Body格式是 {@link JsonObject} 内容的请求，同时包含了 `key` 的信息，近似于
     *          {
     *              "key": "????"
     *          }
     *        - {@link ApiSpec#BODY_NONE}：没有任何 Body 内容的请求。
     *        - {@link ApiSpec#BODY_STRING}：Body格式是一个字符串类似的请求。
     *     2. 第一优先级，直接使用 `module` 参数，此参数是最高优先级。
     *        第二优先级，若 `module` 无法解析，则父从表模式可直接根据 reference 计算
     *     *：如果是父主表模式，此处会跳过 reference 解析，原因很简单，在非 reference 模式下，父主表模式需要针对
     *     子模型进行检索（搜索固定的子模型），而在 reference 模式下（父从表），除非制定 module 参数，否则有定义时
     *     就只会存在唯一的情况，不需要关联模型的检索，可直接绑定（所以作为第二优先级）。简单说第一优先级交给用户自己
     *     来决定，第二优先级是系统自动计算。
     * </code></pre>
     *
     * @param envelop {@link Envelop} 请求的统一资源模型
     *
     * @return {@link String} 返回解析的 module 信息
     */
    public String inputConnected(final Envelop envelop) {
        /*
         * 所有的API都支持第一参数：actor，这是路径定义解析的结果，
         * 所以此处的 `module` 参数代表（ 主模型：active ）。
         ***/
        final String actor = Ux.getString(envelop);
        this.actor = actor;
        this.active = IxMod.of(actor).envelop(envelop);
        String module = null;
        if (ApiSpec.BODY_JSON == this.apiSpecification) {
            // actor       body                key         module          view
            // 0           1 ( JObject )
            // 0           1 ( JCriteria )                  2              3
            this.bodyJ = Ux.getJson1(envelop);
            module = Ux.getString2(envelop);
            this.view = Ux.getVis(envelop, 3);
        } else if (ApiSpec.BODY_STRING == this.apiSpecification) {
            // actor       body                key         module          view
            // 0           1 ( filename )                   2
            // 0                               1
            this.key = Ux.getString1(envelop);
            module = Ux.getString2(envelop);
        } else if (ApiSpec.BODY_ARRAY == this.apiSpecification) {
            // actor       body                key         module          view
            // 0           1 ( JArray )
            // 0           1 ( JArray )                     2
            this.bodyA = Ux.getArray1(envelop);
            module = Ux.getString2(envelop);
        } else if (ApiSpec.BODY_WITH_KEY == this.apiSpecification) {
            // actor       body                key         module          view
            // 0           2                   1
            this.key = Ux.getString1(envelop);
            this.bodyJ = Ux.getJson2(envelop);
        } else if (ApiSpec.BODY_NONE == this.apiSpecification) {
            // actor       body                key         module          view
            // 0                                            1              2
            module = Ux.getString1(envelop);
            this.view = Ux.getVis2(envelop);
        }

        // 若视图为 null，则使用默认视图 [DEFAULT, DEFAULT]
        if (Objects.isNull(this.view)) {
            this.view = KView.smart(null);
        }

        // 此处计算的 module 就是最终的被连接模型
        if (Ut.isNotNil(module)) {
            return module;
        }

        // 第二优先级
        final KJoin join = this.active.connect();
        if (Objects.nonNull(join) && EmDS.Connect.PARENT_STANDBY == join.refer()) {
            // reference 的解析
            final KPoint point = join.getReference();
            module = Objects.isNull(point) ? null : point.indent();
        }
        return module;
    }

    public IxMod inputConnected(final String connected) {
        // Connecting the standBy instance
        final IxJunc junc = IxJunc.of(this.active);
        IxMod standBy = null;
        if (Objects.isNull(connected)) {
            if (Objects.nonNull(this.bodyJ)) {
                // By InJson
                standBy = junc.connect(this.bodyJ);
            } else if (Objects.nonNull(this.bodyA)) {
                // By Array
                standBy = junc.connect(this.bodyA);
            }
        } else {
            // By Module
            standBy = junc.connect(connected);
        }
        return standBy;
    }

    public IxRequest build(final Envelop envelop) {
        /* 关联模型解析 */
        final String connected = this.inputConnected(envelop);

        /* 构造 standBy 的模型 */
        this.standBy = this.inputConnected(connected);

        LOG.Web.info(this.getClass(), LOGGER_MOD,
            this.active.module().identifier(),
            Objects.nonNull(this.standBy) ? this.standBy.module().identifier() : null,
            this.apiSpecification, this.view.view() + ":" + this.view.position());
        return this;
    }

    public IxMod active() {
        return this.active;
    }

    public IxMod standBy() {
        return this.standBy;
    }

    public String inActor() {
        return this.actor;
    }

    public JsonObject dataK() {
        return new JsonObject().put(KName.KEY, this.key);
    }

    public JsonObject dataKJ() {
        return this.bodyJ.copy().put(KName.KEY, this.key);
    }

    public JsonObject dataF() {
        return new JsonObject().put(KName.FILE_NAME, this.key);
    }

    public JsonObject dataJ() {
        return this.bodyJ.copy();
    }

    public JsonObject dataV() {
        return new JsonObject().put(KName.VIEW, this.view);
    }

    public JsonArray dataA() {
        return this.bodyA;
    }
}
