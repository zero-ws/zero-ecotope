package io.zerows.extension.runtime.report.uca.feature;

import io.zerows.constant.VString;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.eon.em.EmReport;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.util.Objects;

/**
 * @author lang : 2024-11-04
 */
public abstract class AbstractFeature implements RFeature {

    protected final EmReport.FeatureType type;
    protected final KpFeature feature;
    protected final Bundle owner;

    protected AbstractFeature(final KpFeature feature) {
        Objects.requireNonNull(feature);
        this.feature = feature;
        this.owner = FrameworkUtil.getBundle(this.getClass());
        this.type = Ut.toEnum(feature::getType, EmReport.FeatureType.class, EmReport.FeatureType.NONE);
    }

    static RFeature of(final KpFeature feature, final Class<?> implCls, final Bundle owner) {
        Objects.requireNonNull(feature);
        final String keyCache = Ut.Bnd.keyCache(owner, implCls) + VString.SLASH + feature.getKey();
        return CC_SKELETON.pick(() -> Ut.instance(implCls, feature), keyCache);
    }

    protected RFeature of(final Class<?> implCls) {
        return of(this.feature, implCls, this.owner);
    }
}
