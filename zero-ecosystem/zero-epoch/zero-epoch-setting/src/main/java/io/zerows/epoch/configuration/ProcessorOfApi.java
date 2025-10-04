package io.zerows.epoch.configuration;

import io.zerows.platform.enums.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfApi extends ProcessorOfHttp {

    @Override
    protected ServerType typeOfHttp() {
        return ServerType.API;
    }
}
