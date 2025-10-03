package io.zerows.epoch.component.setup;

import io.zerows.platform.enums.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfRx extends ProcessorOfHttp {

    @Override
    protected ServerType typeOfHttp() {
        return ServerType.RX;
    }
}
