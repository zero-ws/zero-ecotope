package io.zerows.epoch.bootplus.extension.uca.plugin.ui;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.bootplus.extension.uca.plugin.indent.KeyIndent;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UData;
import io.zerows.extension.mbse.ui.domain.tables.daos.UiVisitorDao;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiVisitor;
import io.zerows.extension.mbse.ui.osgi.spi.ui.UiHunter;
import io.zerows.program.Ux;
import io.zerows.spi.modeler.Identifier;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ControlHunter implements UiHunter {
    private static final LogOf LOGGER = LogOf.get(ControlHunter.class);
    private transient final Identifier indent = new KeyIndent();

    @Override
    public Future<String> seek(final UData data, final UiVisitor visitor) {
        final JsonObject normalized = new JsonObject();
        normalized.put(KName.DATA, data.dataJ());
        /*
         * normalized should be
         * {
         *      "data": {
         *          "categoryFirst": "??",
         *          "categorySecond": "??",
         *          "categoryThird": "??"
         *      }
         * }
         */
        return this.indent.resolve(normalized, data.config()).compose(identifier -> {
            /*
             * {
             *      "type": "组件类型：LIST | FORM",
             *      "sigma": "统一标识符",
             *      "page": "页面ID，对应 UI_PAGE 中的记录",
             *      "path": "三部分组成，前端自动计算的 view / position，配置中的 __ALIAS__ -> alias"
             * }
             *
             * identifier will be re-calculated based join resolution here
             */
            if (Ut.isNil(identifier)) {
                // null controlId returned
                return Ux.future();
            }
            final JsonObject criteria = new JsonObject();
            criteria.put(KName.IDENTIFIER, identifier);
            criteria.put(KName.TYPE, visitor.getType());
            criteria.put(KName.SIGMA, visitor.getSigma());
            criteria.put(KName.App.CONTEXT, visitor.getPath());
            criteria.put(KName.Ui.PAGE, visitor.getPage());

            LOG.Ui.info(LOGGER, "Dynamic Control,  condition = `{0}`", criteria.encode());
            return Ux.Jooq.on(UiVisitorDao.class).<UiVisitor>fetchOneAsync(criteria);
        }).compose(searched -> {
            if (Objects.isNull(searched) || Ut.isNil(searched.getControlId())) {
                /*
                 * controlId = null
                 */
                return Ux.future();
            }
            return Ux.future(searched.getControlId());
        });
    }
}
