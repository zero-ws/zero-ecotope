package io.zerows.component.scanner;

import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.sdk.environment.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerEndPoint implements Inquirer<Set<Class<?>>> {

    public static final String ENDPOINT = "( {0} EndPoint ) The Zero system has found {0} components of @EndPoint.";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> endpoints = clazzes.stream()
            .filter((item) -> item.isAnnotationPresent(EndPoint.class))
            .collect(Collectors.toSet());
        if (!endpoints.isEmpty()) {
            this.logger().info(ENDPOINT, endpoints.size());
        }
        return endpoints;
    }
}
