package io.zerows.module.metadata.zdk.dependency;

import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;

/**
 * 回调专用接口，所有回调统一实现此接口
 *
 * @author lang : 2024-07-01
 */
public interface OCallback<T> {
    // 标准 start 生命周期
    void start(T reference);

    // 标准 stop 生命周期
    default void stop(final T reference) {

    }

    // 满足执行回调的条件
    default boolean isReady() {
        return false;
    }

    default OLog logger() {
        return Ut.Log.service(this.getClass());
    }

    // 专用接口，用于 Object 类型的回调
    interface Standard extends OCallback<Object> {
        // 必须的接口，一定会有多个服务要加载完成，所以必须实现独立的 isReady 接口
        @Override
        boolean isReady();
    }

    interface Signal {

        default boolean isReady() {
            return true;
        }
    }
}
