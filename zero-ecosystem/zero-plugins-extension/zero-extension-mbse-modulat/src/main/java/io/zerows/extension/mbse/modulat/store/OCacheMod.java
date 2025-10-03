package io.zerows.extension.mbse.modulat.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.metadata.KDS;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;
import io.zerows.sdk.management.OCache;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.access.app.HMod;
import io.zerows.specification.vital.HOI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * 新版针对底层配置做出调整，配置接口实现完整的模块化管理流程，以兼容 OSGI 环境，每个 App 会包含一个 Modulat 的相关配置，新版完整架构
 * <pre><code>
 *     {@link HArk} 方舟：应用容器
 *     - {@link HOI} Owner ID，当前应用所属租户信息
 *     - {@link KDS} Database Service，当前应用所拥有的数据库清单 x 5
 *     - {@link HApp} 应用：X_APP 对应配置
 *       - 软关联：appId = OCacheMod = modId-01 = {@link HMod}
 *                                  = modId-02 = HMod
 *                                  = modId-03 = HMod
 *     这个结构替换掉原始的 PowerApp / PowerBlock 结构
 * </code></pre>
 *
 * @author lang : 2024-07-08
 */
public interface OCacheMod extends OCache<HMod> {
    Cc<String, OCacheMod> CC_SKELETON = Cc.open();

    static OCacheMod of(final String appId, final Bundle owner) {
        final String cacheKey = Ut.Bnd.keyCache(owner, OCacheModAmbiguity.class) + VString.SLASH + appId;
        return CC_SKELETON.pick(() -> new OCacheModAmbiguity(owner), cacheKey);
    }

    static OCacheMod of(final String appId) {
        final Bundle owner = FrameworkUtil.getBundle(OCacheMod.class);
        return of(appId, owner);
    }
}
