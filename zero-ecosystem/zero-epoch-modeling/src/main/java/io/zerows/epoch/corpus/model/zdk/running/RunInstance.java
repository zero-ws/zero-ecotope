package io.zerows.epoch.corpus.model.zdk.running;

import java.io.Serializable;

/**
 * @author lang : 2024-05-03
 */
public interface RunInstance<INSTANCE> extends Serializable {

    String name();

    boolean isOk();

    INSTANCE instance();

    <T extends RunInstance<INSTANCE>> T instance(INSTANCE instance);

    <C> C config();

    boolean isOk(int hashCode);
}
