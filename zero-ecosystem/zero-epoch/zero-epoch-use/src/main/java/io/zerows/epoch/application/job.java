package io.zerows.epoch.application;

/**
 * @author lang : 2023-05-29
 */
interface YmlJob {
    String __KEY = "job";
    String STORE = "get";
    String CLIENT = "client";
    String INTERVAL = "interval";

    interface client extends YmlOption.component {

    }

    interface interval extends YmlOption.component {

    }

    interface store extends YmlOption.component {

    }
}
