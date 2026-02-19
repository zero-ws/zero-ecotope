package io.zerows.extension.module.modulat.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.modulat.domain.tables.BAuthority;
import io.zerows.extension.module.modulat.domain.tables.BBlock;
import io.zerows.extension.module.modulat.domain.tables.BWeb;

import java.util.List;
import java.util.Map;

public class TypeOfModulatJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // BAuthority
            Map.of(
                BAuthority.B_AUTHORITY.LIC_ACTION.getName(), BAuthority.B_AUTHORITY.getName(),
                BAuthority.B_AUTHORITY.LIC_PERMISSION.getName(), BAuthority.B_AUTHORITY.getName(),
                BAuthority.B_AUTHORITY.LIC_RESOURCE.getName(), BAuthority.B_AUTHORITY.getName(),
                BAuthority.B_AUTHORITY.LIC_VIEW.getName(), BAuthority.B_AUTHORITY.getName()
            ),
            // BBlock
            Map.of(
                BBlock.B_BLOCK.LIC_IDENTIFIER.getName(), BBlock.B_BLOCK.getName(),
                BBlock.B_BLOCK.LIC_MENU.getName(), BBlock.B_BLOCK.getName(),
                BBlock.B_BLOCK.UI_OPEN.getName(), BBlock.B_BLOCK.getName()
            ),
            // BWeb
            Map.of(
                BWeb.B_WEB.LIC_CONTENT.getName(), BWeb.B_WEB.getName(),
                BWeb.B_WEB.LIC_MODULE.getName(), BWeb.B_WEB.getName(),
                BWeb.B_WEB.LIC_OP.getName(), BWeb.B_WEB.getName(),
                BWeb.B_WEB.LIC_TPL.getName(), BWeb.B_WEB.getName()
            )
        );
    }
}
