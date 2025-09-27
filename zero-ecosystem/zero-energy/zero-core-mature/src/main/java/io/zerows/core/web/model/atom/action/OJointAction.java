package io.zerows.core.web.model.atom.action;

import io.zerows.core.web.model.eon.em.EmAction;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 方法区域专用链接器，用来存储
 * <pre><code>
 *     name = Method 的专用配置
 * </code></pre>
 *
 * @author lang : 2024-04-21
 */
public class OJointAction implements Serializable {

    private final EmAction.JoinPoint type;

    private final ConcurrentMap<String, Method> method = new ConcurrentHashMap<>();

    private OJointAction(final EmAction.JoinPoint type) {
        this.type = type;
    }

    public static OJointAction of(final EmAction.JoinPoint type) {
        return new OJointAction(type);
    }

    public EmAction.JoinPoint type() {
        return this.type;
    }
    // ----------------- IPC Connect --------------------

    public void put(final String name, final Method method) {
        this.method.put(name, method);
    }

    public void put(final ConcurrentMap<String, Method> method) {
        this.method.putAll(method);
    }

    public void remove(final String name) {
        this.method.remove(name);
    }

    public Method get(final String name) {
        return this.method.get(name);
    }

    public ConcurrentMap<String, Method> methods() {
        return this.method;
    }

    public OJointAction add(final OJointAction action) {
        final ConcurrentMap<String, Method> input = action.methods();
        this.method.putAll(input);
        return this;
    }

    public void remove(final OJointAction action) {
        final ConcurrentMap<String, Method> input = action.methods();
        this.method.keySet().removeAll(input.keySet());
    }
}
