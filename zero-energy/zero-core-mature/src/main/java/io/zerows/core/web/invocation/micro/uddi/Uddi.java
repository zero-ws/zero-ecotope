package io.zerows.core.web.invocation.micro.uddi;

import io.zerows.core.exception.web._500InternalServerException;
import io.zerows.core.util.Ut;

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
            throw new _500InternalServerException(caller, "Null or not UddiJet");
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
            throw new _500InternalServerException(caller, "Null or not UddiClient");
        } else {
            return Ut.instance(componentCls, caller);
        }
    }
}
