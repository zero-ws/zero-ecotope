package io.zerows.extension.module.mbseapi.metadata;


import io.zerows.extension.module.mbseapi.component.JtAiakos;
import io.zerows.extension.module.mbseapi.component.JtMinos;

public interface JtConstant {
    String EVENT_ADDRESS = "Πίδακας δρομολογητή://EVENT-JET/ZERO/UNIFORM";
    /*
     * Component Default
     * - Worker
     * - Consumer
     */
    Class<?> COMPONENT_DEFAULT_WORKER = JtMinos.class;
    Class<?> COMPONENT_DEFAULT_CONSUMER = JtAiakos.class;

    String K_PREFIX_JET = "[ XMOD ] ( Jet )";
}
