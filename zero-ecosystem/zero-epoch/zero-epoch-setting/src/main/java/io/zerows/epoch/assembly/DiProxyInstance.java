package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;

import java.lang.reflect.Proxy;

public class DiProxyInstance {

    private static final Cc<Class<?>, DiProxyInstance> CC_V_INSTANCE = Cc.open();
    private final Class<?> interfaceCls;

    private DiProxyInstance(final Class<?> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    public static DiProxyInstance create(final Class<?> interfaceCls) {
        return CC_V_INSTANCE.pick(() -> new DiProxyInstance(interfaceCls), interfaceCls);
    }

    @SuppressWarnings("unchecked")
    public <T> T proxy() {
        final Class<?>[] interfaces = new Class<?>[]{this.interfaceCls};
        return (T) Proxy.newProxyInstance(this.interfaceCls.getClassLoader(), interfaces, new DiProxyInvoker());
    }
}
