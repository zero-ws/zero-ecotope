package io.zerows.extension.mbse.basement.uca.dao.internal;

import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;
import io.zerows.specification.modeling.HAtom;

/**
 * 绑定接口
 * 写数据：Partakor
 */
public interface AoBinder<T extends AoBinder> {
    /**
     * 绑定一个 AoSentence 引用
     */
    T on(final AoSentence sentence);

    /**
     * 绑定一个 AoConnection 引用
     */
    T on(final AoConnection connection);

    /**
     * 绑定一个 Atom 元数据
     */
    T on(final HAtom atom);
}
