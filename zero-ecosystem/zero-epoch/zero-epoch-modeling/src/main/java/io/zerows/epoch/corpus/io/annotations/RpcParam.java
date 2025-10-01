package io.zerows.epoch.corpus.io.annotations;

import java.lang.annotation.*;

/**
 * Rpc parameter, getNull data from remote
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcParam {
}
