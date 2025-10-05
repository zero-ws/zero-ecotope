package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-06
 */
@Data
public class YmExcel implements Serializable {

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> pen;

    private String temp;

    private String tenant;
}
