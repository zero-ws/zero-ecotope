package io.zerows.extension.mbse.basement.osgi.spi.mixture;

import io.zerows.corpus.exception._80510Exception404ModelNotFound;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.eon.AoCache;
import io.zerows.platform.metadata.KPivot;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.operation.HLoad;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HLoadAtom implements HLoad {

    @Override
    public HAtom atom(final String appName, final String identifier) {
        try {

            final HAmbient ambient = KPivot.running();
            final HArk ark = ambient.running(appName);
            // ark.app().connect(Ao.toNS(appName));
            final String unique = ark.cached(identifier);
            final AoPerformer performer = AoPerformer.getInstance(appName);
            final Model model = AoCache.CC_MODEL.pick(() -> performer.fetch(identifier), unique);
            return new DataAtom(model);
        } catch (final _80510Exception404ModelNotFound ignored) {
            /*
             * 这里的改动主要基于动静态模型同时操作导致，如果可以找到Model则证明模型存在于系统中，这种
             * 情况下可直接初始化DataAtom走标准流程，否则直接返回null引用，使得系统无法返回正常模型，
             * 但不影响模型本身的执行。
             */
            return null;
        }
    }
}
