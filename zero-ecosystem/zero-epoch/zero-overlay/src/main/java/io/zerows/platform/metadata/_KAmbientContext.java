package io.zerows.platform.metadata;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.exception._40103Exception500ConnectAmbient;
import io.zerows.support.base.UtBase;
import io.zerows.specification.atomic.HBelong;
import io.zerows.specification.app.HArk;
import io.zerows.specification.cloud.HFrontier;
import io.zerows.specification.cloud.HGalaxy;
import io.zerows.specification.cloud.HSpace;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-06
 */
class _KAmbientContext {
    private static final Cc<String, HArk> CC_ARK = Cc.open();

    _KAmbientContext() {
    }

    HArk running() {
        final Collection<HArk> arks = CC_ARK.get().values();
        if (VValue.ONE == arks.size()) {
            return arks.iterator().next();
        }
        throw new _40103Exception500ConnectAmbient();
    }

    HArk running(final String cacheKey) {
        return Optional.ofNullable(cacheKey)
            .map(CC_ARK.get()::get)
            .orElse(null);
    }

    ConcurrentMap<String, HArk> app() {
        return CC_ARK.get();
    }

    EmApp.Mode registry(final HArk ark) {
        final String cacheKey = UtBase.keyApp(ark);
        CC_ARK.get().put(cacheKey, ark);
        // 注册结束后编织应用的上下文
        // 环境中应用程序超过 1 个时才执行其他判断
        final ConcurrentMap<String, HArk> store = CC_ARK.get();
        final HBelong belong = ark.owner();
        EmApp.Mode mode = EmApp.Mode.CUBE;
        if (VValue.ONE < store.size()) {
            if (belong instanceof HFrontier) {
                mode = EmApp.Mode.FRONTIER;        // Frontier
            } else if (belong instanceof HGalaxy) {
                mode = EmApp.Mode.GALAXY;          // Galaxy
            } else if (belong instanceof HSpace) {
                mode = EmApp.Mode.SPACE;           // Space
            }
        }
        return mode;
    }
}
