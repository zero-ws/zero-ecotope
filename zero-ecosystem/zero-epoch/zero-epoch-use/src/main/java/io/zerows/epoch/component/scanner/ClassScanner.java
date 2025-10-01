package io.zerows.epoch.component.scanner;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.program.Ut;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * @author lang : 2024-04-17
 */
public interface ClassScanner {

    Cc<String, ClassScanner> CCT_SCANNER = Cc.openThread();

    static ClassScanner of() {
        return CCT_SCANNER.pick(ClassScannerBridge::new, ClassScannerBridge.class.getName());
    }

    Set<Class<?>> scan(Bundle bundle);

    default OLog logger() {
        return Ut.Log.metadata(this.getClass());
    }

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
                && Modifier.isPublic(type.getModifiers())               // Ko non-public
                // Ko abstract class, because interface is abstract, single condition is invalid
                && !(Modifier.isAbstract(type.getModifiers()) && !type.isInterface())
                && !Modifier.isStatic(type.getModifiers())              // Ko Static
                && !Throwable.class.isAssignableFrom(type)              // Ko Exception
                && !type.isAnnotationPresent(RunWith.class)             // Ko Test Class
                && isValidMember(type);                          // Ko `Method/Field`
        }
    }
}
