package io.zerows.module.configuration.uca.setup;

import io.zerows.ams.constant.em.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfRx extends ProcessorOfHttp {

    @Override
    protected ServerType typeOfHttp() {
        return ServerType.RX;
    }
}
