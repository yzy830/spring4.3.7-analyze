package com.gerald.analyze.annotation.StandardAnnotationMetadata;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface A {
	String property() default "A";

	String packageName() default "package-A";

	String value() default "value-A";
}
