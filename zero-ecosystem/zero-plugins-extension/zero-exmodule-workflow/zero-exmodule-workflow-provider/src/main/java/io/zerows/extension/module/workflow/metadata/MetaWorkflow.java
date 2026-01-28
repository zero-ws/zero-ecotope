package io.zerows.extension.module.workflow.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.Database;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.web.MDConfig;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.platform.apps.KDS;
import io.zerows.support.Ut;
import lombok.Data;

import java.util.Set;

/**
 * The workflow config instance for deployment
 * the data came from `yml` file instead of `json`
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class MetaWorkflow implements MDConfig {
    private transient String name;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private transient JsonArray builtIn = new JsonArray();

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private transient JsonArray resource = new JsonArray();

    public Set<String> camundaBuiltIn() {
        return Ut.toSet(this.builtIn);
    }

    public Set<String> camundaResource() {
        return Ut.toSet(this.resource);
    }

    public Database camundaDatabase() {
        return KDS.findCamunda();
    }

    @Override
    public String toString() {
        return "MetaWorkflow{" +
            "name='" + this.name + '\'' +
            ", builtIn=" + this.builtIn +
            ", resource=" + this.resource +
            '}';
    }
}
