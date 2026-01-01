package io.zerows.component.module;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lang : 2025-10-13
 */
public abstract class AbstractHActor implements HActor {

    @Override
    public <T> Future<Boolean> startAsync(final HConfig config, final T containerRef) {
        if (containerRef instanceof final Vertx vertxRef) {
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            return vertxRef.executeBlocking(() -> {
                    // 1) 先在 worker 线程里打印一遍
                    logger.info("{}    🐦‍🔥 ---> 运行 actor = `{}` / hash = {} | thread={}",
                        this.vLogColor(), this.getClass().getName(), this.hashCode(), Thread.currentThread().getName());
                    return true; // Callable 必须返回一个值，这里随便给 true
                })
                // 2) 然后继续你原来的异步逻辑（回到 Vert.x Future 链）
                .compose(ignored -> {
                    // 缩进
                    final Future<Boolean> executed = this.startAsync(config, vertxRef);
                    if (executed == null) {
                        logger.warn("{}    ❗ ---> Actor = `{}` 执行失败，返回值为 null！",
                            this.vLogColor(), this.getClass().getName());
                        return Future.succeededFuture(false);
                    }
                    return executed;
                })
                .recover(e -> {
                    logger.error("{}    ❗ ---> Actor = `{}` 执行异常",
                        this.vLogColor(), this.getClass().getName(), e);
                    return Future.failedFuture(e);
                });
        }
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected void vLog(final String message, final Object... params) {
        final Logger logger = this.vLog();
        final Object[] parameters = this.elementConcat(this.vLogColor(), params);
        logger.info("{}        \uD83D\uDCA4 ---> " + message, parameters);
    }

    protected Logger vLog() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected String vLogColor() {
        return "[ PLUG ]";
    }

    private Object[] elementConcat(final Object obj, final Object[] array) {
        if (array == null) {
            // 如果原数组为 null，直接返回包含单个元素的新数组
            return new Object[]{obj};
        }
        // 1. 创建一个长度为原数组长度 + 1 的新数组
        // Arrays.copyOf 会复制原数组内容到新数组的前 array.length 个位置
        // Object[] newArray = Arrays.copyOf(array, array.length + 1);
        // 2. 将原数组内容向后移动一位 (System.arraycopy 是为了通用性，这里其实可以直接赋值)
        //    实际上，Arrays.copyOf 已经把原数组内容放在了 [0, array.length) 位置
        //    我们只需要将 obj 放在索引 0，然后将 [0, array.length) 的内容移动到 [1, array.length+1)
        //    但更简单的做法是，将 [0, array.length) 保留在 [0, array.length)，然后在末尾 (array.length) 放 obj
        //    或者，先在末尾放 obj (这一步 Arrays.copyOf 已经做了，新位置是 null)，然后将 [0, array.length) 整体向后移，
        //    最后在 0 位置放 obj。
        //    最符合“obj 在第一个”的逻辑是：
        //    1. 创建长度为 array.length + 1 的数组
        //    2. 将 obj 放在新数组索引 0
        //    3. 将 array 的所有元素复制到新数组的 [1, array.length+1) 位置

        // 重新实现逻辑：
        // a. 创建长度为 array.length + 1 的数组
        final Object[] resultArray = new Object[array.length + 1];
        // b. 将 obj 放在第一个位置 (索引 0)
        resultArray[0] = obj;
        // c. 将原数组 array 的内容复制到 resultArray 的 [1, array.length+1) 位置
        System.arraycopy(array, 0, resultArray, 1, array.length);

        return resultArray;
    }

    protected abstract Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef);
}
