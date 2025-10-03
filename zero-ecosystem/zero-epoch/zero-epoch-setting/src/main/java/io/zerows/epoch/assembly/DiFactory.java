package io.zerows.epoch.assembly;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;

/**
 * 针对 {@link Injector} 的专用方法，用来生成不同环境的 DI 信息，和原始的 Class 直接区分开，可兼容在 OSGI 环境中的各种操作。
 *
 * @author lang : 2024-05-01
 */
public interface DiFactory {

    Cc<String, Injector> CC_SKELETON = Cc.open();

    static DiFactory singleton() {
        return DiFactoryImpl.of();
    }

    Injector build();

    Injector refresh();
}
