package jakarta.ws.rs.extension;

import io.zerows.cortex.webflow.ResolverJson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {
    /**
     * Default resolver to process the regionInput
     */
    Class<?> resolver() default ResolverJson.class;
}
