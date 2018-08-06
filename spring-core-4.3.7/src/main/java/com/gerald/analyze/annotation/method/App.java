package com.gerald.analyze.annotation.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Method元数据于方法重写
 *
 * (1) 在字节码层面，当派生类重写基类方法，派生类生成当字节码中，会重写包含对这个方法对完整描述，包括修饰符、code属性和annotation属性等；
 *     当派生类没有重写基类方法时(即继承了基类方法当实现)，派生类当字节码中不包含对这个方法的任何描述
 *
 * (2) 在运行时，当派生类重写基类方法时，派生类的Method元数据和基类当Method元数据是不同当，例如Declaring Class、annotation等均不同，
 *     在java中，method不会继承基类method的annotation；当派生类没有重写基类方法时，派生类的Method元数据和基类相等，但是需要使用
 *     equals方法比较，两个类的Method元数据是不同的对象
 *
 * (3) 使用基类的Method，可以对派生类对象调用invoke，与多态兼容
 *
 * */
public class App {
	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method intMethod = Interface.class.getMethod("test");
		Method superMethod = SuperClass.class.getMethod("test");
		Method subMethod = SubClass.class.getMethod("test");
		Method subMethod2 = SubClass2.class.getMethod("test");

		printMethod(Interface.class, intMethod);
		printMethod(SuperClass.class, superMethod);
		printMethod(SubClass.class, subMethod);
		printMethod(SubClass2.class, subMethod2);

		System.out.println("====================================================");
		System.out.println(MessageFormat.format("intMethod {0} superMethod", (intMethod == superMethod? "==":"!=")));
		System.out.println(MessageFormat.format("intMethod {0} superMethod", (intMethod.equals(superMethod)? "equals":"not equals")));
		System.out.println(MessageFormat.format("superMethod {0} subMethod", (superMethod == subMethod? "==":"!=")));
		System.out.println(MessageFormat.format("superMethod {0} subMethod", (superMethod.equals(subMethod)? "equals":"not equals")));
		System.out.println(MessageFormat.format("subMethod {0} subMethod2", (subMethod == subMethod2? "==":"!=")));
		System.out.println(MessageFormat.format("subMethod {0} subMethod2", (subMethod.equals(subMethod2)? "equals":"not equals")));
		System.out.println("");
		System.out.println("");


		System.out.println("====================================================");
		SuperClass superClass = new SuperClass();
		SubClass2 subClass2 = new SubClass2();
		System.out.println("call superclass = " + intMethod.invoke(superClass));
		System.out.println("call superclass = " + intMethod.invoke(subClass2));
	}

	private static void printMethod(Class<?> source, Method method) {
		System.out.println("====================method info from <" + source.getName() + ">=======================");
		System.out.println("declareing class = " + method.getDeclaringClass());
		System.out.println("name = " + method.getName());
		for(Annotation annotation : method.getAnnotations()) {
			printAnnotation(annotation);
		}
		System.out.println("");
		System.out.println("");
	}

	private static void printAnnotation(Annotation annotation) {
		System.out.println("annotation type = " + annotation.annotationType());
	}
}
