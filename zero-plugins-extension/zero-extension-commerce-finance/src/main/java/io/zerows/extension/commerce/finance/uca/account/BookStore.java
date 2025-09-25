package io.zerows.extension.commerce.finance.uca.account;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;

/**
 * @author lang : 2024-01-19
 */
interface BookStore {
    @Memory(Book.class)
    Cc<String, Book> CCT_BOOK = Cc.openThread();
}
