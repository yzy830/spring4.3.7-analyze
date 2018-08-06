package com.gerald.analyze.annotation.StandardAnnotationMetadata;

import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Set;

@D
public class Foo {
	@C
	public void test() {}

	public static class Sub extends Foo {
		@B
		public void test() {}
	}

	public static void main(String[] args) {
		StandardAnnotationMetadata standardAnnotationMetadata = new StandardAnnotationMetadata(Sub.class);

		// 测试获得一个指定annotation的meta annotation
		Set<String> metas = standardAnnotationMetadata.getMetaAnnotationTypes("com.gerald.analyze.annotation.StandardAnnotationMetadata.D");

		System.out.println("D's meta annotations: " + metas);

		System.out.println("has direct D: " + standardAnnotationMetadata.hasAnnotation("com.gerald.analyze.annotation.StandardAnnotationMetadata.D"));
		System.out.println("has meta D: " + standardAnnotationMetadata.hasMetaAnnotation("com.gerald.analyze.annotation.StandardAnnotationMetadata.D"));
		System.out.println("has direct A: " + standardAnnotationMetadata.hasAnnotation("com.gerald.analyze.annotation.StandardAnnotationMetadata.A"));
		System.out.println("has meta A: " + standardAnnotationMetadata.hasMetaAnnotation("com.gerald.analyze.annotation.StandardAnnotationMetadata.A"));


		System.out.println("is annotated with D: " + standardAnnotationMetadata.isAnnotated("com.gerald.analyze.annotation.StandardAnnotationMetadata.D"));
		System.out.println("is annotated with B: " + standardAnnotationMetadata.isAnnotated("com.gerald.analyze.annotation.StandardAnnotationMetadata.B"));
		System.out.println("is annotated with A: " + standardAnnotationMetadata.isAnnotated("com.gerald.analyze.annotation.StandardAnnotationMetadata.A"));

		System.out.println("has B annotated method: " + standardAnnotationMetadata.hasAnnotatedMethods("com.gerald.analyze.annotation.StandardAnnotationMetadata.B"));
		System.out.println("has C annotated method: " + standardAnnotationMetadata.hasAnnotatedMethods("com.gerald.analyze.annotation.StandardAnnotationMetadata.C"));

		System.out.println("===========================================");
		Map<String, Object> annotationAttributes = standardAnnotationMetadata.getAnnotationAttributes("com.gerald.analyze.annotation.StandardAnnotationMetadata.A", false);
		MultiValueMap<String, Object> multiValueMap = standardAnnotationMetadata.getAllAnnotationAttributes("com.gerald.analyze.annotation.StandardAnnotationMetadata.A");
		System.out.println("property of A from getAnnotationAttributes = " + annotationAttributes.get("property"));
		System.out.println("property of A from getAllAnnotationAttributes = " + multiValueMap.get("property"));
		System.out.println("packageName of A from getAnnotationAttributes = " + annotationAttributes.get("packageName"));
		System.out.println("packageName of A from getAllAnnotationAttributes = " + multiValueMap.get("packageName"));
		System.out.println("value of A from getAnnotationAttributes = " + annotationAttributes.get("value"));
		System.out.println("value of A from getAllAnnotationAttributes = " + multiValueMap.get("value"));
	}
}
