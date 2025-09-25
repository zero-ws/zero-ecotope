package io.zerows.extension.runtime.workflow.atom.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.core.util.Ut;
import io.zerows.core.database.atom.Database;
import io.zerows.extend.jackson.databind.JsonArrayDeserializer;
import io.zerows.extend.jackson.databind.JsonArraySerializer;

import java.util.Set;

/**
 * The workflow config instance for deployment
 * the data came from `yml` file instead of `json`
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MetaWorkflow {
    private transient String name;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private transient JsonArray builtIn = new JsonArray();

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private transient JsonArray resource = new JsonArray();

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public JsonArray getBuiltIn() {
        return this.builtIn;
    }

    public void setBuiltIn(final JsonArray builtIn) {
        this.builtIn = builtIn;
    }

    public JsonArray getResource() {
        return this.resource;
    }

    public void setResource(final JsonArray resource) {
        this.resource = resource;
    }

    public Set<String> camundaBuiltIn() {
        return Ut.toSet(this.builtIn);
    }

    public Set<String> camundaResource() {
        return Ut.toSet(this.resource);
    }

    public Database camundaDatabase() {
        return Database.getCamunda();
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
