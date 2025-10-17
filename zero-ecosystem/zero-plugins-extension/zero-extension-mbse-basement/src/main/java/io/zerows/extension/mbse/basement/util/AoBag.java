package io.zerows.extension.mbse.basement.util;

import io.vertx.core.json.JsonArray;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class AoBag implements Serializable {
    /** Model identifier for data bag */
    private String identifier;
    /** The data in current package */
    private JsonArray data = new JsonArray();
    /** The data size of current package **/
    private Integer size = 0;
}
