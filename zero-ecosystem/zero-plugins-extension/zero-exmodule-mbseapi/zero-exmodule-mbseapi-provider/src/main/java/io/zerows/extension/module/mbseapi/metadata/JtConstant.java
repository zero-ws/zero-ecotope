package io.zerows.extension.module.mbseapi.metadata;


import io.zerows.extension.module.mbseapi.component.JtAiakos;
import io.zerows.extension.module.mbseapi.component.JtMinos;

public interface JtConstant {
    /* Default namespace web by JtApp */
    String NAMESPACE_PATTERN = "zero.jet.{0}";
    String EVENT_ADDRESS = "Πίδακας δρομολογητή://EVENT-JET/ZERO/UNIFORM";
    /*
     * Component Default
     * - Worker
     * - Consumer
     */
    Class<?> COMPONENT_DEFAULT_WORKER = JtMinos.class;
    Class<?> COMPONENT_DEFAULT_CONSUMER = JtAiakos.class;
    /*
     * findRunning component extract key
     */
    String COMPONENT_INGEST_KEY = "zero.jet.param.ingest";

    String DEFAULT_POOL_DATABASE = "OX_MULTI_APP_DATABASE";


    String BUNDLE_SYMBOLIC_NAME = "zero-extension-mbse-action";

    String K_PREFIX_JET = "[ XMOD ] ( Jet )";
}
