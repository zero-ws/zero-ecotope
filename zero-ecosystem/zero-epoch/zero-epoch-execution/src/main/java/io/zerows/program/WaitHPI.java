package io.zerows.program;

import cn.hutool.core.util.StrUtil;
import com.google.inject.Injector;
import io.zerows.epoch.assembly.DiFactory;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * 开启 DI 框架之后的 HPI 操作，统一调用 Ux.waitService
 */
class WaitHPI {

    static <T> T waitService(final Class<T> clazz, final String name) {
        final T serviceReference;
        if (StrUtil.isEmpty(name)) {
            // 查找优先级最高的
            serviceReference = HPI.findOneOf(clazz);
        } else {
            // 直接查找指定名称的 SPI 实现
            serviceReference = HPI.findOne(clazz, name);
        }
        if (Objects.isNull(serviceReference)) {
            return null;
        }
        // DI
        final Injector injector = DiFactory.singleton().build();
        injector.injectMembers(serviceReference);
        return serviceReference;
    }
}
