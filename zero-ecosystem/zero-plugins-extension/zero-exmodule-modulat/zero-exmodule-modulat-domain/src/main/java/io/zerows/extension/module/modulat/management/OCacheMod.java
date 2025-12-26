package io.zerows.extension.module.modulat.management;

import io.r2mo.base.dbe.DBS;
import io.r2mo.typed.cc.Cc;
import io.zerows.platform.constant.VString;
import io.zerows.platform.management.OCache;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HMod;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.vital.HOI;
import io.zerows.spi.HPI;

/**
 * 新版针对底层配置做出调整，配置接口实现完整的模块化管理流程，以兼容 OSGI 环境，每个 App 会包含一个 Modulat 的相关配置，新版完整架构
 * <pre><code>
 *     {@link HArk} 方舟：应用容器
 *     - {@link HOI} Owner ID，当前应用所属租户信息
 *     - {@link DBS} Database Service，当前应用所拥有的数据库清单 x N
 *     - {@link HApp} 应用：X_APP 对应配置
 *       - 软关联：id = OCacheMod = modId-01 = {@link HMod}
 *                               = modId-02 = HMod
 *                               = modId-03 = HMod
 *     这个结构替换掉原始的 PowerApp / PowerBlock 结构
 * </code></pre>
 *
 * @author lang : 2024-07-08
 */
public interface OCacheMod extends OCache<HMod> {
    Cc<String, OCacheMod> CC_SKELETON = Cc.open();

    static OCacheMod of(final String appId, final HBundle owner) {
        final String cacheKey = HBundle.id(owner, OCacheModAmbiguity.class) + VString.SLASH + appId;
        return CC_SKELETON.pick(() -> new OCacheModAmbiguity(owner), cacheKey);
    }

    static OCacheMod of(final String appId) {
        final HBundle owner = HPI.findBundle(OCacheMod.class);
        return of(appId, owner);
    }
}
