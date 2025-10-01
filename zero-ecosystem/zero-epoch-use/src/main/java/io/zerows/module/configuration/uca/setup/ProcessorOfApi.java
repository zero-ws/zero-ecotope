package io.zerows.module.configuration.uca.setup;

import io.zerows.epoch.enums.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfApi extends ProcessorOfHttp {

    @Override
    protected ServerType typeOfHttp() {
        return ServerType.API;
    }
}
