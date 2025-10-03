package io.zerows.component.uddi;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.platform.enums.Etat;

import java.util.Set;

/*
 * Zero UddiRegistry center for registry
 */
public interface UddiRegistry {

    /*
     * Initialize
     */
    void initialize(Class<?> clazz);

    /*
     * Route registry
     */
    void registryRoute(final String name, final HttpServerOptions options, final Set<String> routes);

    /*
     * Etat registry
     */
    void registryHttp(final String service, final HttpServerOptions options, final Etat etat);
}
