package io.zerows.epoch.component.uddi;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.zerows.epoch.program.Ut;

import java.util.Objects;

public class Uddi {
    /*
     * Registry interface connect
     */
    public static UddiRegistry registry(final Class<?> caller) {
        final Class<?> componentCls = UddiConfig.registry();
        if (Objects.isNull(componentCls)) {
            return Ut.singleton(UddiEmpty.class);
        } else {
            return Ut.singleton(componentCls, caller);
        }
    }

    /*
     * Discovery
     */
    public static UddiJet discovery(final Class<?> caller) {
        final Class<?> componentCls = UddiConfig.jet();
        if (Objects.isNull(componentCls) || !Ut.isImplement(componentCls, UddiJet.class)) {
            throw new _500ServerInternalException("[ R2MO ] 空组件 或非 UddiJet 类型：" + caller.getName());
        } else {
            return Ut.instance(componentCls);
        }
    }

    /*
     * Client
     */
    public static UddiClient client(final Class<?> caller) {
        final Class<?> componentCls = UddiConfig.client();
        if (Objects.isNull(componentCls) || !Ut.isImplement(componentCls, UddiClient.class)) {
            throw new _500ServerInternalException("[ R2MO ] 组件为空 或 UddiClient 类型错误：" + caller.getName());
        } else {
            return Ut.instance(componentCls, caller);
        }
    }
}
