package io.zerows.extension.module.mbsecore.plugins;

import io.zerows.extension.module.mbsecore.boot.AoCache;
import io.zerows.extension.module.mbsecore.metadata.Model;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.mbse.exception._80510Exception404ModelNotFound;
import io.zerows.platform.apps.KPivot;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
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
