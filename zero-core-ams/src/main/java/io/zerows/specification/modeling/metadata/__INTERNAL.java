package io.zerows.specification.modeling.metadata;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.spi.modeler.MetaOn;


interface CACHE {
    Cc<String, MetaOn> CCT_META_ON = Cc.openThread();
}
