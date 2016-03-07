package pbt4j.annotations;

import java.lang.annotation.*;


/**
 * @author OZY on 2016.03.07.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface JsonData {

    /**
     * Wraps Js values in Java.asJSONCompatible().
     * NOTE: works only from JDK 8u60+
     * @return Json scripts
     */
    String[] value();

}
