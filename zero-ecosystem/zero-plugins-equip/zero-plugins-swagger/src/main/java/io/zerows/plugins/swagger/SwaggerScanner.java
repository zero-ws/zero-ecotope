package io.zerows.plugins.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.zerows.epoch.management.OCacheClass;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class SwaggerScanner {

    public static Set<Class<?>> scan() {
        try {
            final Set<Class<?>> allClasses = OCacheClass.entireValue();
            final Set<Class<?>> interfaces = new HashSet<>();

            for (final Class<?> cls : allClasses) {
                try {
                    if (!cls.isInterface()) {
                        continue;
                    }

                    final boolean hasPath = cls.isAnnotationPresent(Path.class);
                    boolean hasOperation = false;

                    final Method[] methods = cls.getDeclaredMethods();
                    for (final Method method : methods) {
                        try {
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
                        } catch (Exception e) {
                            // 跳过有问题的单个方法
                            System.err.println("Warning: Failed to process method " + method.getName() + " in class " + cls.getName() + ": " + e.getMessage());
                        }
                    }

                    if (hasPath && hasOperation) {
                        interfaces.add(cls);
                    }
                } catch (Exception e) {
                    // 跳过有问题的类
                    System.err.println("Warning: Failed to process class " + cls.getName() + ": " + e.getMessage());
                }
            }

            return interfaces;
        } catch (Exception e) {
            System.err.println("Error during Swagger annotation scanning: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * 检查方法参数是否有 Swagger 注解
     */
    private static boolean hasParameterAnnotations(final Method method) {
        try {
            // 使用 try-catch 避免反射操作阻塞
            final java.lang.reflect.Parameter[] parameters = method.getParameters();
            for (final java.lang.reflect.Parameter param : parameters) {
                if (param.isAnnotationPresent(Parameter.class)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 如果反射操作失败，跳过参数检查
            System.err.println("Warning: Failed to get parameters for method " + method.getName() + ": " + e.getMessage());
        }
        return false;
    }
}
