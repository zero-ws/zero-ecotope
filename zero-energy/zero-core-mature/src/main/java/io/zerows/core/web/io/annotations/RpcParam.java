package io.zerows.core.web.io.annotations;

import java.lang.annotation.*;

/**
 * Rpc parameter, getNull data from remote
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcParam {
}
