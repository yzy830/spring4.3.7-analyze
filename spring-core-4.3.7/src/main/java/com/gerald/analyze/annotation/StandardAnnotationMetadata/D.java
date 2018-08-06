package com.gerald.analyze.annotation.StandardAnnotationMetadata;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@B
@C
@Inherited
public @interface D {
	@AliasFor(annotation = B.class, attribute = "name")
	String nameD() default "D";
}
