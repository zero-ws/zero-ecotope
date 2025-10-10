package io.zerows.specification.configuration;

import io.zerows.platform.enums.EmApp;

/**
 * @author lang : 2023-05-31
 */
public interface HEnergy {

    void initialize();

    HConfig boot(EmApp.LifeCycle lifeCycle);

    String[] args();

    HSetting setting();
}
