package io.zerows.epoch.jigsaw;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.exception._60050Exception501NotSupport;
import org.osgi.framework.Bundle;

import java.util.concurrent.ConcurrentMap;

/**
 * 内部接口，根据路径和 {@link Bundle} 构造对象专用接口，父子接口操作
 * <pre><code>
 *     1. 输入1：路径信息（可能相对路径可能绝对路径）
 *     2. 输入2：当前操作的 Bundle，如果非 OSGI 此值为 null
 * </code></pre>
 *
 * @author lang : 2024-05-12
 */
interface MakerIo<T> {

    @SuppressWarnings("all")
    Cc<String, MakerIo> CC_MAKER = Cc.openThread();

    @SuppressWarnings("unchecked")
    static <T> MakerIo<T> ofConnect() {
        return (MakerIo<T>) CC_MAKER.pick(MakerConnect::new, MakerConnect.class.getName());
    }

    @SuppressWarnings("unchecked")
    static <T> MakerIo<T> ofEntity() {
        return (MakerIo<T>) CC_MAKER.pick(MakerEntity::new, MakerEntity.class.getName());
    }

    ConcurrentMap<String, T> build(String filename, Bundle owner, Object... args);

    default T buildOne(final String filename, final Bundle owner, final Object... args) {
        throw new _60050Exception501NotSupport(this.getClass());
    }
}
