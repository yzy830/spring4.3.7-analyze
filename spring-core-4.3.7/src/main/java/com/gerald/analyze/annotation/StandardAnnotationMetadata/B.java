package com.gerald.analyze.annotation.StandardAnnotationMetadata;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@A
public @interface B {
	@AliasFor(annotation = A.class, attribute = "property")
	String name() default "B";

	String packageName() default "package-B";

	String value() default "value-B";
}
