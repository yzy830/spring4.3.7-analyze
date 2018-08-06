package com.gerald.analyze.annotation.StandardAnnotationMetadata;

import java.lang.annotation.Annotation;

/**
 * annotation不能继承接口，但是可以被接口继承或者被类实现
 * */
public class Test implements A {
	@Override
	public Class<? extends Annotation> annotationType() {
		return null;
	}

	public interface T extends A {

	}
}
