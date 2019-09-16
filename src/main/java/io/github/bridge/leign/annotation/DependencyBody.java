package io.github.bridge.leign.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(DependencyBodies.class)
public @interface DependencyBody {
    String value() default "";
}
