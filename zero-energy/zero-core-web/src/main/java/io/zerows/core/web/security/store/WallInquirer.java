package io.zerows.core.web.security.store;

import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.uca.extract.ExtractorEvent;
import io.zerows.core.web.security.exception.BootWallDuplicatedException;
import io.zerows.core.web.security.exception.BootWallKeyMissingException;
import io.zerows.core.web.security.exception.BootWallMethodDuplicatedException;
import io.zerows.core.web.security.exception.BootWallTypeWrongException;
import io.zerows.module.assembly.uca.di.DiPlugin;
import io.zerows.module.metadata.uca.logging.OLog;
import io.zerows.module.metadata.zdk.uca.Inquirer;
import io.zerows.module.security.annotations.Authenticate;
import io.zerows.module.security.annotations.Authorized;
import io.zerows.module.security.annotations.AuthorizedResource;
import io.zerows.module.security.annotations.Wall;
import io.zerows.module.security.atom.Aegis;
import io.zerows.module.security.atom.AegisItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * This class is for @Wall of security here.
 */
public class WallInquirer implements Inquirer<Set<Aegis>> {

    private final static OLog LOGGER = Ut.Log.security(WallInquirer.class);
    private static final DiPlugin PLUGIN = DiPlugin.create(ExtractorEvent.class);

    @Override
    public Set<Aegis> scan(final Set<Class<?>> walls) {
        /* 1. Build result **/
        final Set<Aegis> wallSet = new TreeSet<>();
        final Set<Class<?>> wallClass = walls.stream()
            .filter((item) -> item.isAnnotationPresent(Wall.class))
            .collect(Collectors.toSet());
        if (!wallClass.isEmpty()) {
            /*
             * It means that you have set Wall and enable security configuration
             * wall Class verification, in this branch it means that the system scanned
             * class that has been annotated with @Wall, you have defined wall
             * of zero framework in your system.
             *
             * Attention: If you enable zero extension ( zero-rbac ), the system will
             * use standard wall class in zero framework, this feature has been upgraded
             * from vertx 4.0
             */
            this.verifyDuplicated(wallClass);
            wallClass.stream().map(this::create).forEach(wallSet::add);
        }

        return wallSet;
    }

    private Aegis create(final Class<?> clazz) {
        final Aegis aegis = new Aegis();
        /*
         * 「Validation」
         * 1 - Proxy Creation with Wall Specification
         * 2 - Wall Type & Aegis Item
         ***/
        this.verifyProxy(clazz, aegis);

        final Annotation annotation = clazz.getAnnotation(Wall.class);
        final String typeKey = Ut.invoke(annotation, "value");
        this.verifyConfig(clazz, aegis, typeKey);
        aegis.setPath(Ut.invoke(annotation, "path"));

        /*
         * AuthorizationHandler class here
         */
        final Class<?> handlerCls = Ut.invoke(annotation, "handler");
        if (AuthorizationHandler.class.isAssignableFrom(handlerCls)) {
            aegis.setHandler(handlerCls);
        }
        /* Verify */
        return aegis;
    }

    private void verifyConfig(final Class<?> clazz, final Aegis reference, final String typeKey) {
        final EmSecure.AuthWall wall = EmSecure.AuthWall.from(typeKey);
        /* Wall Type Wrong */
        Fx.outBoot(Objects.isNull(wall), LOGGER, BootWallTypeWrongException.class,
            this.getClass(), typeKey, clazz);
        reference.setType(wall);
        final ConcurrentMap<String, AegisItem> configMap = AegisItem.configMap();
        if (EmSecure.AuthWall.EXTENSION == wall) {
            /* Extension */
            reference.setDefined(Boolean.TRUE);
            configMap.forEach(reference::addItem);
        } else {
            /* Standard */
            reference.setDefined(Boolean.FALSE);
            final AegisItem found = configMap.getOrDefault(wall.key(), null);
            Fx.outBoot(Objects.isNull(found), LOGGER, BootWallKeyMissingException.class,
                this.getClass(), wall.key(), clazz);
            reference.setItem(found);
        }
    }

    /*
     * Wall class specification scanned and verified by zero framework
     * the class must contain method `@Authenticate` and optional method `@Authorization` once
     */
    private void verifyProxy(final Class<?> clazz, final Aegis reference) {
        final Method[] methods = clazz.getDeclaredMethods();
        // Duplicated Method checking
        Fx.outBoot(this.verifyMethod(methods, Authenticate.class), LOGGER,
            BootWallMethodDuplicatedException.class, this.getClass(),
            Authenticate.class.getSimpleName(), clazz.getName());
        Fx.outBoot(this.verifyMethod(methods, Authorized.class), LOGGER,
            BootWallMethodDuplicatedException.class, this.getClass(),
            Authorized.class.getSimpleName(), clazz.getName());
        Fx.outBoot(this.verifyMethod(methods, AuthorizedResource.class), LOGGER,
            BootWallMethodDuplicatedException.class, this.getClass(),
            AuthorizedResource.class.getSimpleName(), clazz.getName());

        /* Proxy **/
        reference.setProxy(PLUGIN.createProxy(clazz, null));
        // Find the first: Authenticate
        Arrays.stream(methods).forEach(method -> {
            if (Objects.nonNull(method)) {
                if (method.isAnnotationPresent(Authenticate.class)) {
                    reference.getAuthorizer().setAuthenticate(method);
                }
                if (method.isAnnotationPresent(Authorized.class)) {
                    reference.getAuthorizer().setAuthorization(method);
                }
                if (method.isAnnotationPresent(AuthorizedResource.class)) {
                    reference.getAuthorizer().setResource(method);
                }
            }
        });
    }

    private boolean verifyMethod(final Method[] methods,
                                 final Class<? extends Annotation> clazz) {

        final long found = Arrays.stream(methods)
            .filter(method -> method.isAnnotationPresent(clazz))
            .count();
        // If found = 0, 1, OK
        // If > 1, duplicated
        return 1 < found;
    }

    /*
     * Wall duplicated detection
     * Here the unique key is: path + order, you could not define duplicate wall class
     * Path or Order must be not the same or duplicated.
     **/
    private void verifyDuplicated(final Set<Class<?>> wallClses) {
        final Set<String> dupSet = new HashSet<>();
        // type = define
        wallClses.forEach(item -> {
            final Annotation annotation = item.getAnnotation(Wall.class);
            final Integer order = Ut.invoke(annotation, "order");
            final String path = Ut.invoke(annotation, "path");
            final String wallKey = Ut.encryptSHA256(order + path);
            dupSet.add(wallKey);
        });

        // Duplicated adding.
        Fx.outBoot(dupSet.size() != wallClses.size(), LOGGER,
            BootWallDuplicatedException.class, this.getClass(),
            wallClses.stream().map(Class::getName).collect(Collectors.toSet()));
    }
}
