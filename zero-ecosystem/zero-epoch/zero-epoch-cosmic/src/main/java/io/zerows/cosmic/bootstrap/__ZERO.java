package io.zerows.cosmic.bootstrap;

/**
 * @author lang : 2024-05-03
 */
interface INFO {

    String THREAD_NOT_MATCH = "The threading model is not match, Required = \"{}\", Current = \"{}\"";

    String SUCCESS_STARTED = "( {3} ) The verticle \"{0}\" has been deployed " +
        "{1} instances successfully. id = {2}.";
    String FAILURE_STARTED = "( {4} ) The verticle \"{0}\" has been deployed " +
        "{1} instances failed. id = {2}, cause = {3}.";
    String SUCCESS_STOPPED = "( {2} ) The verticle \"{0}\" has been undeployed " +
        " successfully, id = {1}.";
}
