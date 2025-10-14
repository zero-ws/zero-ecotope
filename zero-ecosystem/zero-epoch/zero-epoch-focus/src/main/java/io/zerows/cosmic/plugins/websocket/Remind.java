package io.zerows.cosmic.plugins.websocket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.zerows.epoch.assembly.DI;
import io.zerows.platform.enums.EmService;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * This worker is for @WebSocket annotation to configure the active sending message
 * for alert ( Internal WebSite Message )
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class Remind implements Serializable {

    private static final DI PLUGIN = DI.create(Remind.class);
    private final Cc<String, Object> cctProxy = Cc.openThread();
    private String name;

    private EmService.NotifyType type;

    private String subscribe;

    private String address;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> input;

    private Class<?> proxy;

    private Method method;

    private boolean secure = Boolean.FALSE;

    public Object getProxy() {
        return this.cctProxy.pick(() -> PLUGIN.createProxy(this.proxy, this.method));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Remind remind = (Remind) o;
        return this.subscribe.equals(remind.subscribe) && this.method.equals(remind.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.subscribe, this.method);
    }

    @Override
    public String toString() {
        return "Remind{" +
            "name='" + this.name + '\'' +
            ", type=" + this.type +
            ", subscribe='" + this.subscribe + '\'' +
            ", address='" + this.address + '\'' +
            ", input=" + this.input +
            ", proxy=" + this.proxy +
            ", method=" + this.method +
            ", secure=" + this.secure +
            '}';
    }
}
