package io.zerows.platform.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.exception._40101Exception500CombineApp;
import io.zerows.specification.app.HApp;
import io.zerows.support.base.UtBase;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author lang : 2023-06-06
 */
@Data
@Accessors(chain = true)
public class KApp implements HApp {

    private String name;
    private final JsonObject configuration = new JsonObject();
    private String ns;
    private String tenant;

    public KApp(final String name) {
        final String nameApp = ENV.of().get(EnvironmentVariable.Z_APP, name);
        // 应用名称
        this.name = nameApp;
        // 名空间
        this.ns = HApp.nsOf(nameApp);
    }

    @Override
    public JsonObject option() {
        return this.configuration;
    }

    @Override
    public void option(final JsonObject configuration, final boolean clear) {
        if (UtBase.isNil(configuration)) {
            return;
        }
        if (clear) {
            this.configuration.clear();
        }
        this.configuration.mergeIn(UtBase.valueJObject(configuration), true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T option(final String key) {
        return (T) this.configuration.getValue(key, null);
    }

    @Override
    public <T> void option(final String key, final T value) {
        this.configuration.put(key, value);
    }

    @Override
    public HApp apply(final HApp target) {
        if (Objects.isNull(target)) {
            return this;
        }
        if (target.equals(this)) {
            this.option().mergeIn(UtBase.valueJObject(target.option()));
            return this;
        } else {
            throw new _40101Exception500CombineApp(this.ns, this.name);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public HApp name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public String ns() {
        return this.ns;
    }

    @Override
    public HApp ns(final String ns) {
        this.ns = ns;
        return this;
    }

    @Override
    public HApp tenant(final String tenant) {
        this.tenant = tenant;
        return this;
    }

    @Override
    public String tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KApp kApp = (KApp) o;
        return Objects.equals(this.name, kApp.name) && Objects.equals(this.ns, kApp.ns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.ns);
    }
}
