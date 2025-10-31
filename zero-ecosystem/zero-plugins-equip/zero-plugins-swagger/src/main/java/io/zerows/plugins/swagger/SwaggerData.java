package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Swagger 数据包装类，用于缓存处理器接口类和 OpenAPI 文档
 *
 * @author lang : 2025-10-17
 */
@Data
public class SwaggerData implements Serializable {
    /**
     * 扫描到的所有处理器接口类（标注了 @Path 的类）
     */
    private Set<Class<?>> handlerClasses;

    /**
     * 生成的 OpenAPI 文档对象
     */
    private OpenAPI openAPI;

    public SwaggerData() {
    }

    public SwaggerData(final Set<Class<?>> handlerClasses, final OpenAPI openAPI) {
        this.handlerClasses = handlerClasses;
        this.openAPI = openAPI;
    }
}

