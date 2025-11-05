package io.zerows.extension.module.report.component;

import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.platform.constant.VString;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-11-04
 */
public abstract class FeatureRBase implements FeatureR {

    protected final EmReport.FeatureType type;
    protected final KpFeature feature;
    protected final HBundle owner;

    protected FeatureRBase(final KpFeature feature) {
        Objects.requireNonNull(feature);
        this.feature = feature;
        this.owner = HPI.findBundle(this.getClass());
        this.type = Ut.toEnum(feature::getType, EmReport.FeatureType.class, EmReport.FeatureType.NONE);
    }

    static FeatureR of(final KpFeature feature, final Class<?> implCls, final HBundle owner) {
        Objects.requireNonNull(feature);
        final String keyCache = HBundle.id(owner, implCls) + VString.SLASH + feature.getKey();
        return CC_SKELETON.pick(() -> Ut.instance(implCls, feature), keyCache);
    }

    protected FeatureR of(final Class<?> implCls) {
        return of(this.feature, implCls, this.owner);
    }
}
