/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.MultiValueMap;

/**
 * {@link AnnotationMetadata} implementation that uses standard reflection
 * to introspect a given {@link Class}.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.5
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

	/**
	 * 使用{@code Class#getAnnotations()}得到，因此这个数组包含了指定类的present annotations,
	 * 即direct present annotations和从父类继承过来的direct present annotations(这些annotation需要使用{@code Inherited}标记)
	 */
	private final Annotation[] annotations;

	private final boolean nestedAnnotationsAsMap;


	/**
	 * Create a new {@code StandardAnnotationMetadata} wrapper for the given Class.
	 * @param introspectedClass the Class to introspect
	 * @see #StandardAnnotationMetadata(Class, boolean)
	 */
	public StandardAnnotationMetadata(Class<?> introspectedClass) {
		this(introspectedClass, false);
	}

	/**
	 * Create a new {@link StandardAnnotationMetadata} wrapper for the given Class,
	 * providing the option to return any nested annotations or annotation arrays in the
	 * form of {@link org.springframework.core.annotation.AnnotationAttributes} instead
	 * of actual {@link Annotation} instances.
	 * @param introspectedClass the Class to introspect
	 * @param nestedAnnotationsAsMap return nested annotations and annotation arrays as
	 * {@link org.springframework.core.annotation.AnnotationAttributes} for compatibility
	 * with ASM-based {@link AnnotationMetadata} implementations
	 * @since 3.1.1
	 */
	public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
		super(introspectedClass);
		/* 
		 * 这里使用了getAnnotation，因此annotations只保存了direct annotaion和从父类集成过来的direct annotation，而不
		 * 包含indirect annotation(repeatable annotation)
		 * */
		this.annotations = introspectedClass.getAnnotations();
		// 是否将nested annotation的配置表示为AnnotationAttributes(本质上市key-value map)，否则将会为Annotation创建代理，
		// 以处理@AliasFor标签
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
	}


	@Override
	public Set<String> getAnnotationTypes() {
		Set<String> types = new LinkedHashSet<String>();
		for (Annotation ann : this.annotations) {
			types.add(ann.annotationType().getName());
		}
		return types;
	}

	@Override
	public Set<String> getMetaAnnotationTypes(String annotationName) {
		return (this.annotations.length > 0 ?
				AnnotatedElementUtils.getMetaAnnotationTypes(getIntrospectedClass(), annotationName) : null);
	}

	/**
	 * 判断类的present annotation是否存在annotationName指定的annotation
	 * */
	@Override
	public boolean hasAnnotation(String annotationName) {
		for (Annotation ann : this.annotations) {
			if (ann.annotationType().getName().equals(annotationName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断类的present annotation的meta annotation是否存在annotationName指定的annotation
	 * */
	@Override
	public boolean hasMetaAnnotation(String annotationName) {
		return (this.annotations.length > 0 &&
				AnnotatedElementUtils.hasMetaAnnotationTypes(getIntrospectedClass(), annotationName));
	}

	/**
	 * 判断类的
	 * (1) present annotation
	 * (2) 或者present annotation的meta annotation上
	 * 是否存在annotationName指定的meta annotation
	 * */
	@Override
	public boolean isAnnotated(String annotationName) {
		return (this.annotations.length > 0 &&
				AnnotatedElementUtils.isAnnotated(getIntrospectedClass(), annotationName));
	}

	/**
	 * 获取类的annotation层次(direct present annotation->direct present meta annotation->inherited annotation->inherited meta annotation)中，
	 * annotationName指定的第一个Annotation属性值
	 * */
	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	/**
	 * 获取类的annotation层次(direct present annotation->direct present meta annotation->inherited annotation->inherited meta annotation)中，
	 * annotationName指定的第一个Annotation属性值
	 *
	 * 如果这个annotation是一个meta annotation，会使用其标注的annotation的别名属性来覆盖
	 *
	 * @param classValuesAsString：将class值表示为class full package name
	 * */
	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return (this.annotations.length > 0 ? AnnotatedElementUtils.getMergedAnnotationAttributes(
				getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
	}

	/**
	 * 获取annotationName指定的所有annotation的属性值
	 * */
	@Override
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	/**
	 * 获取annotationName指定的所有annotation的属性值。
	 *
	 * 与{@link #getAnnotationAttributes(String)}的区别是，这个方法不处理annotation标注层次中的@AliasFor
	 * */
	@Override
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return (this.annotations.length > 0 ? AnnotatedElementUtils.getAllAnnotationAttributes(
				getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
	}

	/**
	 * 判断是否存annotationName指定标签标注的declared method(present annotation或者meta annotation，method不能继承)
	 * */
	@Override
	public boolean hasAnnotatedMethods(String annotationName) {
		try {
			Method[] methods = getIntrospectedClass().getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isBridge() && method.getAnnotations().length > 0 &&
						AnnotatedElementUtils.isAnnotated(method, annotationName)) {
					return true;
				}
			}
			return false;
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
		}
	}

	/**
	 * 获取annotationName指定标签标注的declared method(present annotation或者meta annotation，method不能继承)
	 * */
	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		try {
			Method[] methods = getIntrospectedClass().getDeclaredMethods();
			Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>();
			for (Method method : methods) {
				if (!method.isBridge() && method.getAnnotations().length > 0 &&
						AnnotatedElementUtils.isAnnotated(method, annotationName)) {
					annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
				}
			}
			return annotatedMethods;
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
		}
	}

}
