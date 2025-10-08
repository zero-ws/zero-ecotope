package io.zerows.epoch.application;

/**
 * @author lang : 2023-05-29
 */
interface YmlExtension extends YmlOption.component {
    String __KEY = "extension";
    String REGION = "region";
    String AUDITOR = "auditor";
    String ATOM = "argument";
    String ETCD = "etcd";

    interface region extends YmlOption.component {
        interface config {
            String PREFIX = "prefix";
        }
    }

    interface auditor extends YmlOption.component {
        interface config {
            String INCLUDE = "include";
            String EXCLUDE = "exclude";
        }
    }
}
