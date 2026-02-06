package io.zerows.extension.crud.plugins;

import io.r2mo.openapi.metadata.DocApi;
import io.r2mo.openapi.metadata.DocExtension;
import io.r2mo.openapi.metadata.DocSpec;
import io.r2mo.typed.annotation.SPID;
import io.swagger.v3.oas.models.OpenAPI;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.crud.boot.MDCRUDManager;
import io.zerows.mbse.metadata.KModule;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SPID(DocSpec.DEFAULT_DOC_SPEC)
public class DocExtensionActor implements DocExtension {
    private static final String ACTOR = ":actor";

    @Override
    public boolean isMatch(final DocApi docApi) {
        final Method method = docApi.invoker();
        final Adjust adjust = method.getDeclaredAnnotation(Adjust.class);
        if (Objects.isNull(adjust) || KWeb.ORDER.MODULE != adjust.value()) {
            /*
             * - 没有 Adjust 注解的跳过
             * - Adjust 注解值不为 MODULE 的跳过
             */
            return false;
        }

        final String path = docApi.path();
        return path.contains(ACTOR);
    }

    @Override
    public Set<DocApi> compile(final OpenAPI openAPI, final DocApi resource) {
        // Module 模块级的配置要特殊处理
        final Set<KModule> modules = MDCRUDManager.of().getActor();
        final String path = resource.path();
        // 每个模块进行 Path 的提取
        final Set<DocApi> apiSet = new HashSet<>();
        modules.forEach(module -> {
            final String actor = module.getName();
            final String normalized = path.replace(ACTOR, actor);

            final DocApi apiDoc = new DocApi();
            apiDoc.tag(module.getTag());
            apiDoc.path(normalized);
            apiDoc.method(resource.method());
            apiDoc.invoker(resource.invoker());
            apiSet.add(apiDoc);
        });
        return apiSet;
    }
}
