package pbt4j.annotations;

import java.lang.annotation.*;

/**
 * @author Linas on 2016.03.05.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface JsData {

    String[] value();

}
