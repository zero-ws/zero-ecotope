package io.zerows.sdk.plugins;

import com.google.inject.Key;
import io.r2mo.SourceReflect;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 此类的子类通常会包含两个静态方法
 * <pre>
 *     static Xxx of() {
 *         return INSTANCE;
 *     }
 *
 *     static Xxx of(final Vertx vertx, final HConfig config) {
 *         if (INSTANCE == null) {
 *             INSTANCE = new Xxx(vertx, config);
 *         }
 *         return INSTANCE;
 *     }
 * </pre>
 * 其中 Xxx 是子类名称，用于构造单例对象：
 * <pre>
 *     1. 启动周期：
 *        of(Vertx,HConfig) 方法用于首次创建单例对象，在 {@link HActor} 启动时创建，由于 {@link AddOn} 本身是单例模式，
 *        一旦创建之后，INSTANCE 就不会再变更。
 *     2. 请求周期：
 *        of() 方法用于后续获取单例对象，直接返回 INSTANCE，通常在普通场景下使用。
 * </pre>
 * AddOn 组件会分为两种，用于创建真实环境中的 Client 对象
 * <pre>
 *     1. Client 为自定义对象，直接从当前类继承
 *     2. Client 为 Vert.x 组件，继承自 {@link AddOnVertx} -> 特殊 Client，无法追加 {@link AddOn.Name} 注解
 * </pre>
 *
 * @author lang : 2025-10-16
 */
public abstract class AddOnBase<DI> implements AddOn<DI> {

    protected final Class<DI> clazzDi;
    private final Vertx vertx;
    private final HConfig config;

    protected AddOnBase(final Vertx vertx, final HConfig config) {
        this.vertx = vertx;
        this.config = config;
        this.clazzDi = SourceReflect.classT0(this.getClass());
    }

    @Override
    public Key<DI> getKey() {
        return Key.get(this.clazzDi);
    }

    protected abstract AddOnManager<DI> manager();

    protected abstract DI createInstanceBy(final String name);

    protected Vertx vertx() {
        return this.vertx;
    }

    protected HConfig config() {
        return this.config;
    }

    protected JsonObject options() {
        if (Objects.isNull(this.config)) {
            return new JsonObject();
        }
        final JsonObject options = this.config.options();
        return Objects.isNull(options) ? new JsonObject() : options;
    }

    @Override
    public DI createSingleton() {
        final AddOn.Name name = this.clazzDi.getDeclaredAnnotation(AddOn.Name.class);
        if (Objects.isNull(name)) {
            final Logger log = LoggerFactory.getLogger(this.getClass());
            log.warn("[ ZERO ] DI 接口 {} 未定义默认名称，无法创建单例对象", this.clazzDi.getName());
            return null;
        }
        return this.manager().get(name.value(), this::createInstance);
    }

    @Override
    public DI createInstance(final String name) {
        final DI instance = this.createInstanceBy(name);
        this.manager().put(name, instance);
        return instance;
    }
}
