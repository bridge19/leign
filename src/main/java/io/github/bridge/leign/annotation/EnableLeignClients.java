package io.github.bridge.leign.annotation;

import io.github.bridge.leign.core.LeignClientAutoConfiguredRegistrer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(LeignClientAutoConfiguredRegistrer.class)
public @interface EnableLeignClients {
    String[] basePackages() default {};
}
