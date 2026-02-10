package io.zerows.extension.module.tpl.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.tpl.domain.tables.MyDesktop;
import io.zerows.extension.module.tpl.domain.tables.MyFavor;
import io.zerows.extension.module.tpl.domain.tables.MyNotify;
import io.zerows.extension.module.tpl.domain.tables.TplModel;
import io.zerows.extension.module.tpl.domain.tables.TplTicket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeOfTplJsonObject extends TypeOfJsonObject {
    @Override
    protected List<Map<String, String>> regexMeta() {
        final Map<String, String> modelTpl = new HashMap<>();
        modelTpl.put(TplModel.TPL_MODEL.TPL_ACL.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_ACL_VISIT.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_API.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_CATEGORY.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_ENTITY.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_INTEGRATION.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_JOB.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_MODEL_.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_UI.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_UI_FORM.getName(), TplModel.TPL_MODEL.getName());
        modelTpl.put(TplModel.TPL_MODEL.TPL_UI_LIST.getName(), TplModel.TPL_MODEL.getName());

        return List.of(
            // MyDesktop
            Map.of(
                MyDesktop.MY_DESKTOP.UI_CONFIG.getName(), MyDesktop.MY_DESKTOP.getName(),
                MyDesktop.MY_DESKTOP.UI_GRID.getName(), MyDesktop.MY_DESKTOP.getName()
            ),
            // MyFavor
            Map.of(
                MyFavor.MY_FAVOR.URI_PARAM.getName(), MyFavor.MY_FAVOR.getName()
            ),
            // MyNotify
            Map.of(
                MyNotify.MY_NOTIFY.CONFIG_EMAIL.getName(), MyNotify.MY_NOTIFY.getName(),
                MyNotify.MY_NOTIFY.CONFIG_INTERNAL.getName(), MyNotify.MY_NOTIFY.getName(),
                MyNotify.MY_NOTIFY.CONFIG_SMS.getName(), MyNotify.MY_NOTIFY.getName()
            ),
            // TplModel
            modelTpl,
            // TplTicket
            Map.of(
                TplTicket.TPL_TICKET.RECORD_JSON.getName(), TplTicket.TPL_TICKET.getName(),
                TplTicket.TPL_TICKET.UI_CONFIG.getName(), TplTicket.TPL_TICKET.getName()
            )
        );
    }
}
