package io.zerows.epoch.based.configure;

import io.zerows.constant.VOption;

/**
 * @author lang : 2023-05-29
 */
interface YmlJob {
    String __KEY = "job";
    String STORE = "get";
    String CLIENT = "client";
    String INTERVAL = "interval";

    interface client extends VOption.component {

    }

    interface interval extends VOption.component {

    }

    interface store extends VOption.component {

    }
}
