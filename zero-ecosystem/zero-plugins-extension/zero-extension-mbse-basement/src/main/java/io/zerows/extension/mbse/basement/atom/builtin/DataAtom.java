package io.zerows.extension.mbse.basement.atom.builtin;

import io.zerows.epoch.constant.KName;
import io.zerows.mbse.metadata.AbstractHAtom;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MModel;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.specification.access.app.HApp;

/**
 * 内部使用的元数据分析工具，提供
 * 当前 DataRecord的专用 辅助工具，核心元数据处理工厂
 */
public class DataAtom extends AbstractHAtom {

    public DataAtom(final Model model) {
        super(model);
        // sigma / language / namespace re-bind
        final MModel modelRef = model.dbModel();
        final HApp app = this.ark().app();
        app.option(KName.SIGMA, modelRef.getSigma());
        app.option(KName.LANGUAGE, modelRef.getLanguage());
    }

    @Override
    public DataAtom copy(final String identifier) {
        final HApp app = this.ark().app();
        final String appName = app.name();
        return Ao.toAtom(appName, identifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model model() {
        return super.model();
    }
}
