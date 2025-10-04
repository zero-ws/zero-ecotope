package jakarta.ws.rs.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PointParam {
    /*
     * Point parameters
     * Here are dim configuration
     *
     * 2 - [x,y]
     * 3 - [x,y,z]
     * 4 - [x,y,z,j]
     * */
    String value();
}
