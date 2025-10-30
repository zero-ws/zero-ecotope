package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.ext.web.Router;
import io.zerows.cortex.AxisSwaggerFactory;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.plugins.swagger.management.OCacheSwagger;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;


public class SwaggerPlugin implements AxisSwaggerFactory {

    @Override
    public Axis getAxis() {
        return null;
    }

    @Override
    public boolean isEnabled(final HBundle owner, final RunServer runServer) {
       final NodeNetwork nodeNetwork = NodeStore.ofNetwork();
       final HSetting setting = nodeNetwork.setting();
       final HConfig swaggerConfig = setting.extension("swagger");
        if(swaggerConfig==null){
            return false;
        }
       final Boolean swagger = swaggerConfig.options().getBoolean("enabled");
        if(swagger==null|| !swagger){
            return false;
        }else {
           final Router router = runServer.refRouter();
            // 获取第一个可用的 Swagger 数据
           final SwaggerData data = OCacheSwagger.entireFirst();
            if (data != null) {
               final OpenAPI openAPI = data.getOpenAPI();
                // Step3: 构造路由并挂载
                SwaggerRouteBuilder.mount(router, openAPI);
            }
            return true;
        }
    }

}
