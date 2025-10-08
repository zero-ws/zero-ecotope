package io.zerows.epoch.application;

/**
 * @author lang : 2023-05-29
 */
interface YmlInit {
    String __KEY = "init";
    String CONFIGURE = "configure";
    String COMPILE = "compile";

    interface configure extends YmlOption.component {
        String ORDER = "order";
    }

    interface compile extends YmlOption.component {
        String ORDER = "order";
    }
}
