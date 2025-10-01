package io.zerows.epoch.component.setup;

/**
 * @author lang : 2024-04-20
 */
interface INFO {
    String V_BEFORE = "( node = {0}, type = {1} ) Before Validation {2}.";
    String V_AFTER = "Server = {1}, Port = {2}, Configuration = {0}.";

    String V_DEPLOYMENT = "( Delivery / Deploy ) The `deployment` configuration has not been found, default will be used.";
}
