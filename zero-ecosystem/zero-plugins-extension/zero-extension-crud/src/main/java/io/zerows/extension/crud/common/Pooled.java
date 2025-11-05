package io.zerows.extension.crud.common;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.crud.uca.Agonic;
import io.zerows.extension.crud.uca.IxJunc;
import io.zerows.extension.crud.uca.Tran;
import io.zerows.extension.crud.uca.input.Pre;
import io.zerows.extension.crud.uca.next.Co;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public interface Pooled {
    Cc<String, Agonic> CCT_AGONIC = Cc.openThread();
    Cc<String, Pre> CCT_PRE = Cc.openThread();
    Cc<String, Tran> CCT_TRAN = Cc.openThread();
    Cc<String, Co> CCT_CO = Cc.openThread();

    Cc<String, IxJunc> CCT_JUNC = Cc.openThread();
}
