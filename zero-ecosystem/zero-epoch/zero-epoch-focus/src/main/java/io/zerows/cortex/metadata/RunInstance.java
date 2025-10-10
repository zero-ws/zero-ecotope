package io.zerows.cortex.metadata;

import java.io.Serializable;

/**
 * @author lang : 2024-05-03
 */
public interface RunInstance<INSTANCE> extends Serializable {

    String name();

    boolean isOk();

    INSTANCE instance();

    <RUN extends RunInstance<INSTANCE>> RUN instance(INSTANCE instance);

    <C> C config();

    boolean isOk(int hashCode);
}
