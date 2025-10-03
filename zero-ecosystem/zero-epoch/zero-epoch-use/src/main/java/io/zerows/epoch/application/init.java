package io.zerows.epoch.application;

import io.zerows.platform.constant.VOption;

/**
 * @author lang : 2023-05-29
 */
interface YmlInit {
    String __KEY = "init";
    String CONFIGURE = "configure";
    String COMPILE = "compile";

    interface configure extends VOption.component {
        String ORDER = "order";
    }

    interface compile extends VOption.component {
        String ORDER = "order";
    }
}
