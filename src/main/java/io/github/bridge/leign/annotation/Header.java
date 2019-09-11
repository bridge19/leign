package io.github.bridge.leign.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Repeatable(RepeatedHeader.class)
public @interface Header {
    String value() default "";
}
