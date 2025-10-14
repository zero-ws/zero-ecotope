package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.zerows.specification.development.compiled.HBundle;
import org.junit.runner.RunWith;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * @author lang : 2024-04-17
 */
public interface ClassScanner {

    Cc<String, ClassScanner> CCT_SCANNER = Cc.openThread();

    static ClassScanner of() {
        return CCT_SCANNER.pick(ClassScannerCommon::new, ClassScannerCommon.class.getName());
    }

    Set<Class<?>> scan(HBundle bundle);

    interface Tool {

        @SuppressWarnings("all")
        static boolean isValidMember(final Class<?> type) {
            try {
                // Fix issue of Guice
                // java.lang.NoClassDefFoundError: camundajar/impl/scala/reflect/macros/blackbox/Context
                type.getDeclaredMethods();
                type.getDeclaredFields();
                return true;
            } catch (NoClassDefFoundError ex) {
                return false;
            }
        }

        static boolean isValid(final Class<?> type) {
            return !type.isAnonymousClass()                             // Ko Anonymous
                && !type.isAnnotation()                                 // Ko Annotation
                && !type.isEnum()                                       // Ko Enum
                && !Modifier.isPrivate(type.getModifiers())             // Ko No Private，新版开放 default 域，只有私有类无法被扫描
                && !(Modifier.isAbstract(type.getModifiers()) && !type.isInterface())
                && !Modifier.isStatic(type.getModifiers())              // Ko Static
                && !Throwable.class.isAssignableFrom(type)              // Ko Exception
                && !type.isAnnotationPresent(RunWith.class)             // Ko Test Class
                && isValidMember(type);                          // Ko `Method/Field`
        }
    }
}
