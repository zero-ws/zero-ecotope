package io.zerows.plugins.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.zerows.management.OCacheClass;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SwaggerAnnotationScanner {

    public static Set<Class<?>> scan() {
        final Set<Class<?>> allClasses = OCacheClass.entireValue();
        final Set<Class<?>> interfaces = new HashSet<>();

        for (final Class<?> cls : allClasses) {
            if (!cls.isInterface()) continue;

            final boolean hasPath = cls.isAnnotationPresent(Path.class);
            boolean hasOperation = false;

            for (final Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Path.class) ||
                    method.isAnnotationPresent(GET.class) ||
                    method.isAnnotationPresent(POST.class) ||
                    method.isAnnotationPresent(PUT.class) ||
                    method.isAnnotationPresent(DELETE.class) ||
                    method.isAnnotationPresent(PATCH.class) ||
                    method.isAnnotationPresent(Operation.class) ||
                    method.isAnnotationPresent(RequestBody.class) ||
                    method.isAnnotationPresent(ApiResponse.class) ||
                    method.isAnnotationPresent(Parameter.class) ||
                    method.isAnnotationPresent(Schema.class) ||
                    hasParameterAnnotations(method)) {
                    hasOperation = true;
                    break;
                }
            }

            if (hasPath && hasOperation) {
                interfaces.add(cls);
            }
        }

        return interfaces;
    }

    /**
     * 检查方法参数是否有 Swagger 注解
     */
    private static boolean hasParameterAnnotations(final Method method) {
        for (final java.lang.reflect.Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(Parameter.class)) {
                return true;
            }
        }
        return false;
    }
}
