package io.zerows.module.assembly.uca.di;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;

/**
 * 针对 {@link Injector} 的专用方法，用来生成不同环境的 DI 信息，和原始的 Class 直接区分开，可兼容在 OSGI 环境中的各种操作。
 *
 * @author lang : 2024-05-01
 */
public interface IOCFactory {

    Cc<String, Injector> CC_SKELETON = Cc.open();

    static IOCFactory singleton() {
        return IOCFactoryImpl.of();
    }

    Injector build();

    Injector refresh();
}
