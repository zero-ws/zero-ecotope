package io.zerows.epoch.corpus.mbse;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.application.YmlCore;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.operation.HLoad;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class PluginLoad {
    /*
     * Infusion for HLoad / HED
     */
    private static final Cc<String, HLoad> CC_PLUGIN_ATOM = Cc.openThread();

    public static HAtom atom(final String namespace, final String identifier) {
        return Ux.mountPlugin(YmlCore.extension.ATOM, (atomCls, config) -> {
            final HLoad loader = CC_PLUGIN_ATOM.pick(() -> Ut.instance(atomCls));
            /*
             * Bind configuration of
             * argument:
             *   component:
             *   config
             */
            return loader.atom(namespace, identifier);
        }, () -> null);
    }
}
