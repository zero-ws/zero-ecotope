package io.zerows.cosmic.plugins.websocket;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Subscribe;
import io.zerows.epoch.assembly.ExtractTool;
import io.zerows.epoch.assembly.ExtractToolMethod;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.metadata.XEmptyInstance;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.EmService;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockExtractor implements Extractor<Set<Remind>> {

    public static final String SOCK_HIT = "( Socket ) The socket job {0} will be deployed, socket = `{1}`, address = `{2}`.";

    @Override
    public Set<Remind> extract(final Class<?> clazz) {
        // 1. Class verify
        ExtractTool.verifyNoArgConstructor(clazz);
        ExtractTool.verifyIfPublic(clazz);
        // 2. Scan method to findRunning @WebSocket
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
        final Subscribe annoSubscribe = method.getDeclaredAnnotation(Subscribe.class);
        String address = annoSubscribe.value();
        /*
         * If the address is not start with "/", the system convert the address findRunning
         * from direct address to the normalized path.
         *
         * For example:
         *
         * job/notify       -> /job/notify
         */
        if (!address.startsWith(VString.SLASH)) {
            address = VString.SLASH + address;
        }
        final EmService.NotifyType type = annoSubscribe.type();
        // 2. Build Remind
        final Remind remind = new Remind();
        remind.setMethod(method);
        remind.setSubscribe(address);
        remind.setType(type);

        // Fix: Instance class for proxy
        //        final Object proxy = PLUGIN.createInstance(clazz);
        remind.setProxy(clazz);
        remind.setName(annoSubscribe.name());
        remind.setSecure(annoSubscribe.secure());
        // Input Part: input / inputAddress
        final Address addressAnno = method.getDeclaredAnnotation(Address.class);
        final String inputAddress = addressAnno.value();
        if (Ut.isNotNil(inputAddress)) {
            remind.setAddress(inputAddress);
            final Class<?> inputCls = annoSubscribe.input();
            if (XEmptyInstance.class != inputCls) {
                remind.setInput(inputCls);
            }
        }
        final String sockAddr = address;
        this.logger().info(SOCK_HIT, clazz, sockAddr, inputAddress);
        return remind;
    }
}
