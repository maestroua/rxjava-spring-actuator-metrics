package com.playtika.aop.metrics.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method execution should be metered.
 *
 * @author vkhrushchak
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Gauge {

    /**
     * The name of gauge.
     * <p>If left unspecified, the name of the gauge is the name of class plus the annotated method.
     * If specified, the method name is ignored.
     */
    String value() default "";
}