package io.zerows.core.web.model.zdk;

import io.zerows.epoch.common.shared.datamation.KDictConfig;

/*
 * Service
 */
public interface ServiceDefinition extends Service {

    /*
     * `dictConfig`
     * `dictComponent` of I_SERVICE
     * `dictEpsilon` of I_SERVICE
     * Here `dictComponent` is required if configured.
     * Dictionary configuration for current Job / Component
     */
    KDictConfig dict();
}
