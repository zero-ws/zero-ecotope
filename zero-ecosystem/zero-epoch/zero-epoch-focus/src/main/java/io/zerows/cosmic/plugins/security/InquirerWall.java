package io.zerows.cosmic.plugins.security;

import io.zerows.epoch.assembly.DI;
import io.zerows.epoch.assembly.ExtractorEvent;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.metadata.security.SecurityMeta;

import java.util.Set;

/**
 *
 */
public class InquirerWall implements Inquirer<Set<SecurityMeta>> {

    private static final DI PLUGIN = DI.create(ExtractorEvent.class);

    @Override
    public Set<SecurityMeta> scan(final Set<Class<?>> walls) {
        //        /* 1. Build result **/
        //        final Set<KSecurity> wallSet = new TreeSet<>();
        //        final Set<Class<?>> wallClass = walls.stream()
        //            .filter((item) -> item.isAnnotationPresent(Wall.class))
        //            .collect(Collectors.toSet());
        //        if (!wallClass.isEmpty()) {
        //            /*
        //             * It means that you have set Wall and enable security configuration
        //             * wall Class verification, in this branch it means that the system scanned
        //             * class that has been annotated with @Wall, you have defined wall
        //             * of zero framework in your system.
        //             *
        //             * Attention: If you enable zero extension ( zero-rbac ), the system will
        //             * use standard wall class in zero framework, this feature has been upgraded
        //             * from vertx 4.0
        //             */
        //            this.verifyDuplicated(wallClass);
        //            wallClass.stream().map(this::create).forEach(wallSet::add);
        //        }
        //
        //        return wallSet;
        return Set.of();
    }
    //
    //    private KSecurity create(final Class<?> clazz) {
    //        final KSecurity aegis = new KSecurity();
    //        /*
    //         * 「Validation」
    //         * 1 - Proxy Creation with Wall Specification
    //         * 2 - Wall Type & Aegis Item
    //         ***/
    //        this.verifyProxy(clazz, aegis);
    //
    //        final Annotation annotation = clazz.getAnnotation(Wall.class);
    //        final String typeKey = Ut.invoke(annotation, "value");
    //        this.verifyConfig(clazz, aegis, typeKey);
    //        aegis.setPath(Ut.invoke(annotation, "path"));
    //
    //        /*
    //         * AuthorizationHandler class here
    //         */
    //        final Class<?> handlerCls = Ut.invoke(annotation, "handler");
    //        if (AuthorizationHandler.class.isAssignableFrom(handlerCls)) {
    //            aegis.setHandler(handlerCls);
    //        }
    //        /* Verify */
    //        return aegis;
    //    }
    //
    //    private void verifyConfig(final Class<?> clazz, final KSecurity reference, final String typeKey) {
    //        final SecurityType wall = SecurityType.from(typeKey);
    //        /* Wall Type Wrong */
    //        Fn.jvmKo(Objects.isNull(wall), _40075Exception400WallTypeWrong.class, typeKey, clazz);
    //        reference.setType(wall);
    //        final ConcurrentMap<String, KSecurity.Provider> configMap = KSecurity.Provider.configMap();
    //        if (SecurityType.EXTENSION == wall) {
    //            /* Extension */
    //            reference.setDefined(Boolean.TRUE);
    //            configMap.forEach(reference::addItem);
    //        } else {
    //            /* Standard */
    //            reference.setDefined(Boolean.FALSE);
    //            final KSecurity.Provider found = configMap.getOrDefault(wall.key(), null);
    //            Fn.jvmKo(Objects.isNull(found), _40040Exception400WallKeyMissing.class, wall.key(), clazz);
    //            reference.setItem(found);
    //        }
    //    }
    //
    //    /*
    //     * Wall class specification scanned and verified by zero framework
    //     * the class must contain method `@Authenticate` and optional method `@Authorization` once
    //     */
    //    private void verifyProxy(final Class<?> clazz, final KSecurity reference) {
    //        final Method[] methods = clazz.getDeclaredMethods();
    //        // Duplicated Method checking
    //        Fn.jvmKo(this.verifyMethod(methods, Authenticate.class),
    //            _40041Exception500WallMethodDuplicated.class,
    //            Authenticate.class.getSimpleName(), clazz);
    //        Fn.jvmKo(this.verifyMethod(methods, Authorized.class),
    //            _40041Exception500WallMethodDuplicated.class,
    //            Authorized.class.getSimpleName(), clazz);
    //        Fn.jvmKo(this.verifyMethod(methods, AuthorizedResource.class),
    //            _40041Exception500WallMethodDuplicated.class,
    //            AuthorizedResource.class.getSimpleName(), clazz);
    //
    //        /* Proxy **/
    //        reference.setProxy(PLUGIN.createProxy(clazz, null));
    //        // Find the first: Authenticate
    //        Arrays.stream(methods).forEach(method -> {
    //            if (Objects.nonNull(method)) {
    //                if (method.isAnnotationPresent(Authenticate.class)) {
    //                    reference.getAuthorizer().setAuthenticate(method);
    //                }
    //                if (method.isAnnotationPresent(Authorized.class)) {
    //                    reference.getAuthorizer().setAuthorization(method);
    //                }
    //                if (method.isAnnotationPresent(AuthorizedResource.class)) {
    //                    reference.getAuthorizer().setResource(method);
    //                }
    //            }
    //        });
    //    }
    //
    //    private boolean verifyMethod(final Method[] methods,
    //                                 final Class<? extends Annotation> clazz) {
    //
    //        final long found = Arrays.stream(methods)
    //            .filter(method -> method.isAnnotationPresent(clazz))
    //            .count();
    //        // If found = 0, 1, OK
    //        // If > 1, duplicated
    //        return 1 < found;
    //    }
    //
    //    /*
    //     * Wall duplicated detection
    //     * Here the unique key is: path + order, you could not define duplicate wall class
    //     * Path or Order must be not the same or duplicated.
    //     **/
    //    private void verifyDuplicated(final Set<Class<?>> wallClses) {
    //        final Set<String> dupSet = new HashSet<>();
    //        // type = define
    //        wallClses.forEach(item -> {
    //            final Annotation annotation = item.getAnnotation(Wall.class);
    //            final Integer order = Ut.invoke(annotation, "order");
    //            final String path = Ut.invoke(annotation, "path");
    //            final String wallKey = Ut.encryptSHA256(order + path);
    //            dupSet.add(wallKey);
    //        });
    //
    //        // Duplicated adding.
    //        Fn.jvmKo(dupSet.size() != wallClses.size(),
    //            _40038Exception400WallDuplicated.class,
    //            wallClses.stream().map(Class::getName).collect(Collectors.toSet()));
    //    }
}
