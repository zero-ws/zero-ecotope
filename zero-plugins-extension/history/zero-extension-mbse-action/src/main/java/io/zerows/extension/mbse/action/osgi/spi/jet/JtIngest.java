package io.zerows.extension.mbse.action.osgi.spi.jet;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.uca.param.DataIngest;

/*
 * 「Extension」
 *  When the paramMode is `DEFINE`, it means that you must set the extension class name into
 *  vertx-inject.yml
 *  The key is `zero.jet.param.ingest`
 *  In your content of vertx-inject.yml file, here you should define
 *
 *  zero.jet.param.ingest=xxxxxx
 *
 *  `xxxxx` means class name, the class definition must be:
 *  1) implements JtIngest interface
 *  2) This class must contain public constructor without any arguments
 */
public interface JtIngest {

    Cc<String, JtIngest> CC_INGEST = Cc.openThread();

    static JtIngest getInstance() {
        return CC_INGEST.pick(DataIngest::new);
        // Fn.po?lThread(Pool.POOL_INGEST, DataIngest::new);
    }

    /*
     * Different workflow will call component other
     */
    Envelop in(RoutingContext context, JtUri uri);
}
