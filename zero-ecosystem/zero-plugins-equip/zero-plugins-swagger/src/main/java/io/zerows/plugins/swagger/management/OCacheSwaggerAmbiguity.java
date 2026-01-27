package io.zerows.plugins.swagger.management;

import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.plugins.swagger.SwaggerData;
import io.zerows.specification.development.compiled.HBundle;

/**
 * Swagger 缓存实现类
 *
 * @author lang : 2025-10-17
 */
class OCacheSwaggerAmbiguity extends AbstractAmbiguity implements OCacheSwagger {
    private SwaggerData swaggerData;

    OCacheSwaggerAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public SwaggerData value() {
        return this.swaggerData;
    }

    @Override
    public OCacheSwagger add(final SwaggerData data) {
        this.swaggerData = data;
        return this;
    }

    @Override
    public OCacheSwagger remove(final SwaggerData data) {
        if (this.swaggerData != null && this.swaggerData.equals(data)) {
            this.swaggerData = null;
        }
        return this;
    }
}

