package io.zerows.component.convert;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.annotations.Memory;

/*
 * Vto date here
 */
public interface Vto<T> {

    T to(Object value, Class<?> type);
}

@SuppressWarnings("all")
interface CACHE {
    /**
     * Vto 转换器专用缓存池
     */
    @Memory(Vto.class)
    Cc<String, Vto> CCT_VTO = Cc.openThread();
}