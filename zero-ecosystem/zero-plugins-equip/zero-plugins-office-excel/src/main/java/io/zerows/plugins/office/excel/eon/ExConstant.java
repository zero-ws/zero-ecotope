package io.zerows.plugins.office.excel.eon;

import io.zerows.epoch.based.constant.KName;

/**
 * @author lang : 2024-06-13
 */
public interface ExConstant {

    String K_TYPE = "__type__";
    String K_CONTENT = "__content__";

    interface CELL {
        String UUID = "{UUID}";
        String PWD = "PWD";
        String NAME_CONFIG = "NAME:config";
        String NAME_CLASS = "NAME:class";
        String NAME_ABBR_CONFIG = "NAME_ABBR:config";
        String CODE_CONFIG = "CODE:config";
        String CODE_CLASS = "CODE:class";
        String CODE_NAME_CONFIG = "CODE:NAME:config";

        String P_JSON = "JSON";
        String P_FILE = "FILE";
        String P_PAGE = "PAGE";

        String[] PREFIX = new String[]{P_FILE, P_JSON, P_PAGE};

        String[] PARAM_INPUT = new String[]{
            KName.NAME, KName.CODE, "nameAbbr"
        };
    }
}
