package io.zerows.sdk.plugins;

import com.google.inject.Key;
import io.r2mo.SourceReflect;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author lang : 2025-10-16
 */
public abstract class AddOnBase<DI> implements AddOn<DI> {

    private final Vertx vertx;
    private final HConfig config;
    private final Class<DI> clazzDi;

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
