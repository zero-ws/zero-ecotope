package io.zerows.epoch.corpus.configuration.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.EmDeploy;
import io.zerows.epoch.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.epoch.integrated.jackson.databind.JsonObjectSerializer;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActorOptions implements Serializable {
    @JsonIgnore
    private final ConcurrentMap<String, DeploymentOptions> deploymentMap = new ConcurrentHashMap<>();
    @JsonIgnore
    private DeliveryOptions deliveryOptions = new DeliveryOptions();
    /* Default mode of deployment */
    private EmDeploy.Mode mode = EmDeploy.Mode.CODE;
    /* Options */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject options = new JsonObject();
    /* Options */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject delivery = new JsonObject();

    public EmDeploy.Mode getMode() {
        return this.mode;
    }

    public void setMode(final EmDeploy.Mode mode) {
        this.mode = mode;
    }

    public JsonObject getOptions() {
        return this.options;
    }

    public void setOptions(final JsonObject options) {
        this.options = options;
    }

    public JsonObject getDelivery() {
        return this.delivery;
    }

    public void setDelivery(final JsonObject delivery) {
        this.delivery = delivery;
    }

    public ConcurrentMap<String, DeploymentOptions> optionDeploy() {
        return this.deploymentMap;
    }

    public void optionDeploy(final String className, final DeploymentOptions options) {
        this.deploymentMap.put(className, options);
    }

    public DeliveryOptions optionDelivery() {
        return this.deliveryOptions;
    }

    public void optionDelivery(final DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    @Override
    public String toString() {
        return "ActorOptions{" +
            "mode=" + this.mode +
            ", options=" + this.options +
            ", delivery=" + this.delivery +
            '}';
    }
}
