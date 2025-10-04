package io.zerows.epoch.corpus.web.websocket.store;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.assembly.ExtractToolMethod;
import io.zerows.epoch.assembly.ExtractToolVerifier;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.web.websocket.annotations.Subscribe;
import io.zerows.epoch.corpus.web.websocket.atom.Remind;
import io.zerows.epoch.corpus.web.websocket.eon.em.RemindType;
import io.zerows.epoch.metadata.KEmptyInstance;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockExtractor implements Extractor<Set<Remind>> {

    @Override
    public Set<Remind> extract(final Class<?> clazz) {
        // 1. Class verify
        ExtractToolVerifier.noArg(clazz);
        ExtractToolVerifier.modifier(clazz);
        // 2. Scan method to find @WebSocket
        final Set<Remind> websockets = new HashSet<>();
        final Method[] methods = clazz.getDeclaredMethods();
        Arrays.stream(methods)
            .filter(ExtractToolMethod::isValid)
            .filter(method -> method.isAnnotationPresent(Subscribe.class))
            .map(this::extract)
            .forEach(websockets::add);
        return websockets;
    }

    private Remind extract(final Method method) {
        final Class<?> clazz = method.getDeclaringClass();
        // 1. Scan whole Endpoints
        final Annotation annotation = method.getDeclaredAnnotation(Subscribe.class);
        String address = Ut.invoke(annotation, KName.VALUE);
        /*
         * If the address is not start with "/", the system convert the address value
         * from direct address to the normalized path.
         *
         * For example:
         *
         * job/notify       -> /job/notify
         */
        if (!address.startsWith(VString.SLASH)) {
            address = VString.SLASH + address;
        }
        final RemindType type = Ut.invoke(annotation, KName.TYPE);
        // 2. Build Remind
        final Remind remind = new Remind();
        remind.setMethod(method);
        remind.setSubscribe(address);
        remind.setType(type);

        // Fix: Instance class for proxy
        //        final Object proxy = PLUGIN.createInstance(clazz);
        remind.setProxy(clazz);
        remind.setName(Ut.invoke(annotation, KName.NAME));
        remind.setSecure(Ut.invoke(annotation, "secure"));
        // Input Part: input / inputAddress
        final Annotation annoAddr = method.getDeclaredAnnotation(Address.class);
        final String inputAddress = Ut.invoke(annoAddr, KName.VALUE);
        if (Ut.isNotNil(inputAddress)) {
            remind.setAddress(inputAddress);
            final Class<?> inputCls = Ut.invoke(annotation, "input");
            if (KEmptyInstance.class != inputCls) {
                remind.setInput(inputCls);
            }
        }
        final String sockAddr = address;
        this.logger().info(INFO.SOCK_HIT, clazz, sockAddr, inputAddress);
        return remind;
    }
}
