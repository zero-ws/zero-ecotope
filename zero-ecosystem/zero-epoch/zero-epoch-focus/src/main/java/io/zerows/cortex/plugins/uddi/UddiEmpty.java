package io.zerows.cortex.plugins.uddi;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.platform.enums.EmCloud;

import java.util.Set;

public class UddiEmpty implements UddiRegistry {
    @Override
    public void initialize(final Class<?> clazz) {

    }

    @Override
    public void registryRoute(final String name, final HttpServerOptions options, final Set<String> routes) {

    }

    @Override
    public void registryHttp(final String service, final HttpServerOptions options, final EmCloud.Etat etat) {

    }
}
