package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.QaS;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.assembly.exception._40012Exception500AddressWrong;
import io.zerows.epoch.basicore.JointAction;
import io.zerows.epoch.basicore.WebReceipt;
import io.zerows.epoch.boot.Anno;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.management.OCacheJoint;
import io.zerows.platform.enums.EmDeploy;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Aeon 桥，用于桥接 Zero / Aeon 容器，可直接使用不同注解处理
 * <pre>
 *     1. {@link Queue}  -> Zero Container Worker
 *     2. {@link QaS} -> Aeon Container Worker
 *     3. @Address 注解扫描
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class BridgeForAeon {

    private static final Set<String> ADDRESS = new TreeSet<>();

    static {
        /* 1. Get all endpoints **/
        final Set<Class<?>> endpoints = OCacheClass.entireValue(VertxComponent.ENDPOINT);

        /* 2. Scan for @Address to matching **/
        Observable.fromIterable(endpoints)
            .map(queue -> Anno.query(queue, Address.class))
            // 3. Scan annotations
            .subscribe(annotations -> Observable.fromArray(annotations)
                .map(addressAnno -> Ut.invoke(addressAnno, "value"))
                // 4. Hit address
                .subscribe(address -> ADDRESS.add(address.toString()))
                .dispose())
            .dispose();
        /* 5.Log out address report **/
        log.debug("[ ZERO ] Zero 系统监测到 {} 地址！", ADDRESS.size());
        ADDRESS.forEach(item -> log.debug("\t\tAddr: {}", item));
    }

    /*
     * Aeon / Zero @Address 处理
     * 1. Method 转换成 Receipt 对象
     */
    public static WebReceipt receipt(final Method method) {
        // 1. Scan whole Endpoints
        final Class<?> clazz = method.getDeclaringClass();
        final Address annotation = method.getDeclaredAnnotation(Address.class);
        final String address = annotation.value();
        // 2. Ensure address incoming.
        Fn.jvmKo(!ADDRESS.contains(address), _40012Exception500AddressWrong.class, address, clazz, method);


        // 访问已扫描缓存
        final JointAction action = OCacheJoint.entireJoint().get(EmDeploy.JoinPoint.QAS);
        final Method replaced = action.get(address);


        final WebReceipt receipt = new WebReceipt();
        receipt.setAddress(address);
        if (Objects.isNull(replaced)) {
            // Zero Workflow
            receipt.setMethod(method);
            receipt.setProxy(clazz);
        } else {
            // Aeon Workflow
            receipt.setMethod(replaced);
            receipt.setProxy(replaced.getDeclaringClass());
        }
        return receipt;
    }
}
