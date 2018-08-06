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

import java.util.Set;

/**
 * <pre>
 * {@link AnnotatedTypeMetadata}
 * {@link ClassMetadata}
 *      <- {@code AnnotationMetadata}
 * </pre>
 * 
 * <p>
 * 定义了一个Class的Annotation元数据访问接口。这个接口继承了{@code ClassMetadata}和{@code AnnotatedTypeMeta}，从而
 * 可以获得一个类的基本元数据以及获得一个Annotation的属性信息。<br/><br/>
 * 
 * 这个代码完全使用字符串(annotation name)方式，访问annotation信息，因此，不要求class已经加载
 * </p>
 * 
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.5
 * @see StandardAnnotationMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 * @see AnnotatedTypeMetadata
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

	/**
	 * <p>
	 *     获取Class上的Present Annotation
	 * </p>
	 *
	 * Get the fully qualified class names of all annotation types that
	 * are <em>present</em> on the underlying class.
	 * @return the annotation type names
	 */
	Set<String> getAnnotationTypes();

	/**
	 * <p>
	 *     如果annotationName指定的Annotation present on在AnnotationMetadata描述的Class，
	 *     getMetaAnnotationTypes会返回annotationName Annotation上所有的meta annotation
	 *     以及meta annotation的meta annotation，参考{@link com.gerald.analyze.annotation.StandardAnnotationMetadata.Foo}
	 * </p>
	 *
	 * Get the fully qualified class names of all meta-annotation types that
	 * are <em>present</em> on the given annotation type on the underlying class.
	 * @param annotationName the fully qualified class name of the meta-annotation
	 * type to look for
	 * @return the meta-annotation type names
	 */
	Set<String> getMetaAnnotationTypes(String annotationName);

	/**
	 * <p>
	 *     判断Class的Present Annotation是否存annotationName指定的Annotation
	 * </p>
	 *
	 * Determine whether an annotation of the given type is <em>present</em> on
	 * the underlying class.
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 * @return {@code true} if a matching annotation is present
	 */
	boolean hasAnnotation(String annotationName);

	/**
	 * <p>
	 *     判断Class的Present Annotation或者其meta annotation是否有metaAnnotationName指定的meta annotation
	 * </p>
	 *
	 * Determine whether the underlying class has an annotation that is itself
	 * annotated with the meta-annotation of the given type.
	 * @param metaAnnotationName the fully qualified class name of the
	 * meta-annotation type to look for
	 * @return {@code true} if a matching meta-annotation is present
	 */
	boolean hasMetaAnnotation(String metaAnnotationName);

	/**
	 * Determine whether the underlying class has any methods that are
	 * annotated (or meta-annotated) with the given annotation type.
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 */
	boolean hasAnnotatedMethods(String annotationName);

	/**
	 * Retrieve the method metadata for all methods that are annotated
	 * (or meta-annotated) with the given annotation type.
	 * <p>For any returned method, {@link MethodMetadata#isAnnotated} will
	 * return {@code true} for the given annotation type.
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 * @return a set of {@link MethodMetadata} for methods that have a matching
	 * annotation. The return value will be an empty set if no methods match
	 * the annotation type.
	 */
	Set<MethodMetadata> getAnnotatedMethods(String annotationName);

}
