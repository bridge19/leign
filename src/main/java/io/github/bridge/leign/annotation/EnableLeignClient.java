package io.github.bridge.leign.annotation;

import io.github.bridge.leign.core.LeignClientAutoConfiguredRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(LeignClientAutoConfiguredRegistrar.class)
public @interface EnableLeignClient {
    String[] basePackages() default {};
}
