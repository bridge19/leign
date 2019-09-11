package io.github.bridge.leign.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatedHeader {
    Header[] value() default {};
}
