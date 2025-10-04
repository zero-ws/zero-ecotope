package io.zerows.extension.runtime.crud.uca.desk;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.mbse.metadata.KModule;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.exception._80100Exception404ModuleMissing;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 当前请求专用的模块对象，此模块对象会在请求开始时创建，且创建两种
 * <pre><code>
 *     1. 模型对象本身的创建
 *     2. 关联模型的创建（连接创建connecting）
 * </code></pre>
 * 此处数据结构的核心模型如下：
 * <pre><code>
 *     IxMod（实例）
 *       | -- parameters：    参数（实例）{@link JsonObject}
 *       | -- envelop：       资源模型请求（实例）{@link Envelop}
 *       | -- module：        主模型（引用）{@link KModule}
 *       | -- connect：       辅助模型（引用）{@link KModule}
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class IxMod {
    /**
     * 「实例」
     * 当前请求中访问模型所需的参数信息，注：此参数来自于 {@link Envelop} 的请求数据中的参数绑定，此处
     * 参数主要是当前请求中的参数，所以这里的 parameters 可理解成一个参数的拷贝，直接将请求中的参数拷贝
     * 到 parameters 变量中形成单次请求相关数据。
     */
    private final transient JsonObject parameters = new JsonObject();

    /**
     * 「实例」
     * 当前请求中的请求数据相关信息，此处请求数据是一个完整的 Zero 资源模型 {@link Envelop}，它对应的
     * 请求中所有所需的数据信息，包括 HTTP 请求头、请求体等所有和当前请求相关的内容。
     */
    private transient Envelop envelop;

    /**
     * 「引用」
     * 当前主模型的引用信息，关联到系统中存储的模型，创建直接引用，系统中会根据 identifier 的模型统一标识符
     * 对模型进行池化管理。
     */
    private transient KModule module;

    /**
     * 「引用」
     * 连接模型的引用信息，同属于系统中存储的模型，只是职责不同。
     */
    private transient KModule connect;

    /**
     * 「实例」
     * 当前请求中遇到的各种异常信息，包括所有执行过程中的异常，分为如下：
     * <pre><code>
     *     1. 元数据抽取、配置提取类异常（构造时）
     *     2. 请求类数据异常
     *     3. 其他系统级异常或自定义异常
     * </code></pre>
     */
    private transient WebException error;

    private IxMod(final String actor) {
        final KModule module;
        try {
            module = IxPin.getActor(actor);
            Fn.jvmKo(Objects.isNull(module), _80100Exception404ModuleMissing.class, actor);
            this.module = module;
        } catch (final WebException error) {
            // 自定义异常
            Ix.LOG.Web.fatal(this.getClass(), error);
            this.error = error;
        } catch (final Throwable error) {
            // JVM异常
            Ix.LOG.Web.fatal(this.getClass(), error);
            this.error = new _500ServerInternalException("[ R2MO ] JVM 异常: " + error.getMessage());
        }
    }

    public static IxMod of(final String actor) {
        return new IxMod(actor);
    }

    // ------------------ 元数据定义 ----------------

    /**
     * 「主模型」
     * 返回当前请求关联的主模型 {@link KModule}，注：一个请求中主模型不能为空
     *
     * @return {@link KModule}
     */
    public KModule module() {
        return this.module;
    }

    /**
     * 返回异常信息
     *
     * @return {@link WebException}
     */
    public WebException error() {
        return this.error;
    }

    /**
     * 「子模型」
     * 返回当前请求关联的子模型相关信息，若当前定义中不包含子模型，那么此处返回是 null
     *
     * @return {@link KModule}
     */
    public KModule connected() {
        return this.connect;
    }

    /**
     * 连接点主函数
     *
     * @param in 被连接的 {@link IxMod} 对象
     *
     * @return {@link IxMod}
     */
    public IxMod connected(final IxMod in) {
        this.connect = in.module;
        return this;
    }

    /**
     * 「子模型」
     * 连接点处理，根据当前模型直接提取子模型的连接点信息，通过连接点信息来构造关联关系，这种模式比
     * {@link IxMod#connected()} 的颗粒度更加细一个级别
     *
     * @return {@link KJoin}
     */
    public KJoin connect() {
        return this.module.getConnect();
    }

    /**
     * 「子模型」
     * 动态链接模型的 identifier 信息，这种模式下，identifier 本身是在连接模式下才生效的东西，
     * 此处的子模型本身是在连接时就已经确定的，所以这里的 identifier 是动态生成的。
     *
     * @return {@link String}
     */
    public String connectId() {
        if (this.canJoin()) {
            return this.connect.identifier();
        } else {
            return null;
        }
    }

    /**
     * 此方法定义的当前 {@link IxMod} 专用的缓存键，系统会根据缓存键来存储相关缓存的信息，根据
     * 这个缓存键来构造缓存的键值，这个缓存键值是根据模型本身来构造的。
     * <pre><code>
     *     - module identifier
     *     - module identifier : connect identifier
     * </code></pre>
     *
     * @return {@link String}
     */
    public String cached() {
        final StringBuilder key = new StringBuilder();
        key.append(this.module.identifier());
        if (this.canJoin()) {
            key.append(VString.COLON);
            key.append(this.connect.identifier());
        }
        return key.toString();
    }

    // ------------------ 数据定义 ------------------

    /**
     * 设置请求数据专用方法，设置两部分内容
     * <pre><code>
     *     1. 数据资源模型 {@link Envelop}
     *     2. 参数信息 {@link JsonObject}
     * </code></pre>
     * 参数信息来自两部分：
     * <pre><code class="java">
     *     1. {@link Envelop#headersX()}
     *     2. {@link Envelop#body()}
     * </code></pre>
     *
     * @param envelop {@link Envelop}
     *
     * @return {@link IxMod}
     */
    public IxMod envelop(final Envelop envelop) {
        this.envelop = envelop;

        // 参数处理
        final JsonObject headers = envelop.headersX();
        this.parameters.mergeIn(headers, true);
        final JsonObject parameters = envelop.body();
        if (Ut.isNotNil(parameters)) {
            this.parameters.mergeIn(parameters, true);
        }
        return this;
    }

    /**
     * 返回数据资源模型
     *
     * @return {@link Envelop}
     */
    public Envelop envelop() {
        return this.envelop;
    }

    /**
     * 返回参数信息
     *
     * @return {@link JsonObject}
     */
    public JsonObject parameters() {
        return this.parameters.copy();
    }

    // ------------------ 判断函数 ------------------

    /**
     * 当前模型是否支持 JOIN，从配置上看，是否定义过 connect 节点如：
     * <pre><code>
     *     {
     *         "connect": ????
     *     }
     * </code></pre>
     *
     * @return 是否支持 JOIN
     */
    public boolean canJoin() {
        return Objects.nonNull(this.connect);
    }

    /**
     * 是否支持转换
     *
     * @return 是否支持转换
     */
    public boolean canTransform() {
        return Objects.nonNull(this.module.getTransform());
    }
}
