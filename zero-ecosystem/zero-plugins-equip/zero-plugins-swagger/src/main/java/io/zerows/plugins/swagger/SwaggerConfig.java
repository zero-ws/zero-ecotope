package io.zerows.plugins.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SwaggerConfig implements Serializable {
    @JsonProperty("api-docs")
    private ApiDocs apiDocs = new ApiDocs();

    private String version = "v3";

    private boolean output = true;

    @JsonProperty("swagger-ui")
    private SwaggerUi swaggerUi = new SwaggerUi();

    public boolean isEnabled() {
        return this.apiDocs.getEnabled() && this.swaggerUi.getEnabled();
    }

    @Data
    public static class ApiDocs implements Serializable {
        private Boolean enabled = true;
        private String path = "openapi.yaml";
        private String version = "1.0.0";
        private String title = "Zero Ecotope API Docs";
        private String description = "";
    }

    @Data
    public static class SwaggerUi implements Serializable {
        private Boolean enabled = true;

        private String path = "swagger-ui";

        @JsonProperty("config-url")
        private String configUrl = "swagger-config";

        @JsonProperty("validator-url")
        private String validatorUrl = "";

        @JsonProperty("tags-sorter")
        private String tagsSorter = "alpha";

        @JsonProperty("operations-sorter")
        private String operationsSorter = "method";

        @JsonProperty("doc-expansion")
        private String docExpansion = "none";

        @JsonProperty("display-request-duration")
        private Boolean displayRequestDuration = true;

        private Boolean filter = true;

        @JsonProperty("deep-linking")
        private Boolean deepLinking = true;

        @JsonProperty("try-it-out-enabled")
        private Boolean tryItOutEnabled = true;
    }
}