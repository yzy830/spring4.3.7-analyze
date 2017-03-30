/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * <pre>
 * {@link AbstractBeanDefinition}
 *      <- {@code RootBeanDefinition}
 * </pre>
 * 
 * <p>
 * 表示一个完成合并的BeanDefinition，表示一个特定的Bean。</br></br>
 * 一个{@code RootBeanDefinition}可能来自一个{@code GenericBeanDefinition}，
 * 也可能来自多个相互继承的{@code GenericBeanDefinition}
 * </p>
 * 
 * <p>
 * {@code RootBeanDefinition}涉及到一个Bean如何创建和构造，他需要与{@link DefaultListableBeanFactory}等配合使用，
 * 配合一起使用。这些类在一个package里面，耦合比较紧
 * </p>
 * 
 * A root bean definition represents the merged bean definition that backs
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * from multiple original bean definitions that inherit from each other,
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

	private BeanDefinitionHolder decoratedDefinition;

	private AnnotatedElement qualifiedElement;

	boolean allowCaching = true;

	/**
	 * 标记工厂方法是否被重载过
	 */
	boolean isFactoryMethodUnique = false;

	volatile ResolvableType targetType;

	/** Package-visible field for caching the determined Class of a given bean definition */
	volatile Class<?> resolvedTargetType;

	/** Package-visible field for caching the return type of a generically typed factory method */
	volatile ResolvableType factoryMethodReturnType;

	/** Common lock for the four constructor fields below */
	final Object constructorArgumentLock = new Object();

	/** Package-visible field for caching the resolved constructor or factory method */
	Object resolvedConstructorOrFactoryMethod;

	/** Package-visible field that marks the constructor arguments as resolved */
	boolean constructorArgumentsResolved = false;

	/** Package-visible field for caching fully resolved constructor arguments */
	Object[] resolvedConstructorArguments;

	/** Package-visible field for caching partly prepared constructor arguments */
	Object[] preparedConstructorArguments;

	/** Common lock for the two post-processing fields below */
	final Object postProcessingLock = new Object();

	/** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied */
	boolean postProcessed = false;

	/** Package-visible field that indicates a before-instantiation post-processor having kicked in */
	volatile Boolean beforeInstantiationResolved;

	/**
     * 外部管理配置的属性，作用？
     */
	private Set<Member> externallyManagedConfigMembers;

	/**
	 * 外部管理的初始化方法，应该是由{@link org.springframework.context.annotation.Bean @Bean}配置的init方法？
	 */
	private Set<String> externallyManagedInitMethods;

	/**
     * 外部管理的销毁方法，应该是由{@link org.springframework.context.annotation.Bean @Bean}配置的init方法？
     */
	private Set<String> externallyManagedDestroyMethods;


	/**
	 * Create a new RootBeanDefinition, to be configured through its bean
	 * properties and configuration methods.
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * Create a new RootBeanDefinition for a singleton.
	 * @param beanClass the class of the bean to instantiate
	 * @see #setBeanClass
	 */
	public RootBeanDefinition(Class<?> beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * using the given autowire mode.
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		super(original);
		this.decoratedDefinition = original.decoratedDefinition;
		this.qualifiedElement = original.qualifiedElement;
		this.allowCaching = original.allowCaching;
		this.isFactoryMethodUnique = original.isFactoryMethodUnique;
		this.targetType = original.targetType;
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
	}


	/**
	 * {@code RootBeanDefinition}没有parent，返回null
	 * */
	@Override
	public String getParentName() {
		return null;
	}

	/**
     * {@code RootBeanDefinition}没有parent，抛出异常
     * */
	@Override
	public void setParentName(String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}

	/**
	 * <p>
	 * 被装饰的bean definition
	 * </p>
	 * 
	 * Register a target definition that is being decorated by this bean definition.
	 */
	public void setDecoratedDefinition(BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	/**
	 * <p>
     * 被装饰的bean definition
     * </p>
	 * 
	 * Return the target definition that is being decorated by this bean definition, if any.
	 */
	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}

	/**
	 * <p>
	 * 这个字段什么作用？
	 * </p>
	 * 
	 * Specify the {@link AnnotatedElement} defining qualifiers,
	 * to be used instead of the target class or factory method.
	 * @since 4.3.3
	 * @see #setTargetType(ResolvableType)
	 * @see #getResolvedFactoryMethod()
	 */
	public void setQualifiedElement(AnnotatedElement qualifiedElement) {
		this.qualifiedElement = qualifiedElement;
	}

	/**
	 * <p>
     * 这个字段什么作用？
     * </p>
	 * 
	 * Return the {@link AnnotatedElement} defining qualifiers, if any.
	 * Otherwise, the factory method and target class will be checked.
	 * @since 4.3.3
	 */
	public AnnotatedElement getQualifiedElement() {
		return this.qualifiedElement;
	}

	/**
	 * <p>
	 * 记录包含泛型参数的目标类型
	 * </p>
	 * 
	 * Specify a generics-containing target type of this bean definition, if known in advance.
	 * @since 4.3.3
	 */
	public void setTargetType(ResolvableType targetType) {
		this.targetType = targetType;
	}

	/**
	 * <p>
     * 直接记录目标类型
     * </p>
	 * 
	 * Specify the target type of this bean definition, if known in advance.
	 * @since 3.2.2
	 */
	public void setTargetType(Class<?> targetType) {
		this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
	}

	/**
	 * <p>
	 * 获得目标类型。经过了泛型解析
	 * </p>
	 * 
	 * Return the target type of this bean definition, if known
	 * (either specified in advance or resolved on first instantiation).
	 * @since 3.2.2
	 */
	public Class<?> getTargetType() {
		if (this.resolvedTargetType != null) {
			return this.resolvedTargetType;
		}
		return (this.targetType != null ? this.targetType.resolve() : null);
	}

	/**
	 * <p>
	 * 指定工厂方法名称。使用这个方法指定时，认为工厂方法没有重载，会标记{@link isFactoryMethodUnique}为true
	 * </p>
	 * 
	 * Specify a factory method name that refers to a non-overloaded method.
	 */
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	/**
	 * Check whether the given candidate qualifies as a factory method.
	 */
	public boolean isFactoryMethod(Method candidate) {
		return (candidate != null && candidate.getName().equals(getFactoryMethodName()));
	}

	/**
	 * 获得解析得到的构造器或者工厂方法。这个方法将与BeanFactory配合使用，待分析
	 * 
	 * Return the resolved factory method as a Java Method object, if available.
	 * @return the factory method, or {@code null} if not found or not resolved yet
	 */
	public Method getResolvedFactoryMethod() {
		synchronized (this.constructorArgumentLock) {
			Object candidate = this.resolvedConstructorOrFactoryMethod;
			return (candidate instanceof Method ? (Method) candidate : null);
		}
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedConfigMembers == null) {
				this.externallyManagedConfigMembers = new HashSet<Member>(1);
			}
			this.externallyManagedConfigMembers.add(configMember);
		}
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedConfigMembers != null &&
					this.externallyManagedConfigMembers.contains(configMember));
		}
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedInitMethods == null) {
				this.externallyManagedInitMethods = new HashSet<String>(1);
			}
			this.externallyManagedInitMethods.add(initMethod);
		}
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedInitMethods != null &&
					this.externallyManagedInitMethods.contains(initMethod));
		}
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedDestroyMethods == null) {
				this.externallyManagedDestroyMethods = new HashSet<String>(1);
			}
			this.externallyManagedDestroyMethods.add(destroyMethod);
		}
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedDestroyMethods != null &&
					this.externallyManagedDestroyMethods.contains(destroyMethod));
		}
	}


	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}

}
