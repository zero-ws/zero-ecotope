package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.metadata.MMComponent;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link VertxYml.boot}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmBoot implements Serializable {
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> launcher;
    private MMComponent pre;
    private MMComponent on;
    private MMComponent off;
    private MMComponent run;
}
