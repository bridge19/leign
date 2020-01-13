package io.github.bridge.leign.annotation;

import io.github.bridge.leign.enums.DependecyScope;
import io.github.bridge.leign.enums.DependecyType;
import io.github.bridge.leign.enums.DependencyPersist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Dependency {
    DependecyScope scope() default DependecyScope.NEVER;
    DependencyPersist persist() default DependencyPersist.NEVER;
    DependecyType type() default DependecyType.BY_HEADER;
}
