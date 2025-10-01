package io.zerows.extension.mbse.modulat.uca.dock;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.epoch.enums.modeling.EmModel;

/*
 * Connect to HArk part for configuration in each application
 * This interface may call HArk in future with adapter.
 */
public interface Ark {
    Cc<String, Ark> CC_ARK = Cc.openThread();

    static Ark configure() {
        return CC_ARK.pick(ArkConfigure::new, ArkConfigure.class.getName());
    }

    static Ark bag() {
        return CC_ARK.pick(ArkBag::new, ArkBag.class.getName());
    }

    /*
     * Fetch data from the system by `appId`
     * instead of other modulat.
     */
    default Future<ClusterSerializable> modularize(final String appId, final boolean open) {
        return this.modularize(appId, open, EmModel.By.BY_ID);
    }

    default Future<ClusterSerializable> modularize(final String appId) {
        return this.modularize(appId, false, EmModel.By.BY_ID);
    }

    default Future<ClusterSerializable> modularize(final String appId, final EmModel.By by) {
        return this.modularize(appId, false, by);
    }

    Future<ClusterSerializable> modularize(String appId, boolean open, EmModel.By by);
}
