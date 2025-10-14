package io.zerows.epoch.basicore;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.assembly.DI;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.constant.VString;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * Scanned Uri Event ( KMetadata ) for each Endpoint.
 */
@Data
public class WebEvent implements Serializable {

    private static final DI PLUGIN = DI.create(WebEvent.class);
    private final Cc<String, Object> cctProxy = Cc.openThread();
    /**
     * The uri address for current route
     */
    private String path;
    /**
     * order for current Event
     * It could be modified in latest version by @Adjust
     */
    private int order = KWeb.ORDER.EVENT;
    /**
     * consume mime
     */
    private Set<MediaType> consumes;
    /**
     * produce mime
     */
    private Set<MediaType> produces;
    /**
     * http method.
     */
    private HttpMethod method;
    /**
     * request action ( Will be calculated )
     */
    private Method action;
    /**
     * Proxy instance
     */
    private Class<?> proxy;

    public void setPath(final String path) {
        if (null != path) {
            final String literal = path.trim();
            if (literal.endsWith(VString.SLASH)) {
                this.path = literal.substring(0, literal.length() - 1);
            } else {
                this.path = literal;
            }
        } else {
            this.path = path;
        }
    }

    public Object getProxy() {
        return this.cctProxy.pick(() -> PLUGIN.createProxy(this.proxy, this.action));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final WebEvent event)) {
            return false;
        }
        return this.order == event.order &&
            Objects.equals(this.path, event.path) &&
            this.method == event.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.order, this.method);
    }

    @Override
    public String toString() {
        return "Event{" +
            "path='" + this.path + '\'' +
            ", order=" + this.order +
            ", consumes=" + this.consumes +
            ", produces=" + this.produces +
            ", method=" + this.method +
            ", action=" + this.action +
            ", proxy=" + this.proxy +
            '}';
    }
}
