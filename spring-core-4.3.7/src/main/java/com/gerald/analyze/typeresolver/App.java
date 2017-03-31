package com.gerald.analyze.typeresolver;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.core.ResolvableType;

public class App {
    
    public static void forClass(Class<?> clazz) {
        ResolvableType r = ResolvableType.forClass(clazz);
        
        ResolvableType[] ts = r.getGenerics();
        
        Arrays.stream(ts).map(Object::toString).forEach(System.out::println);
    }
    
    public static void getComponent(Class<?> clazz) {
        ResolvableType r = ResolvableType.forClass(clazz);
        
        if(isNone(r.getComponentType())) {
            System.out.println("is NONE");
        }  else {
            System.out.println("is not NONE");
        }
    }
    
    private static boolean isNone(ResolvableType resolvableType) {
        return resolvableType == ResolvableType.NONE;
    }
    
    public static <T extends List<String>, U extends T, D extends U> void process(D d) {
        
    }
    
    public static Class<?> resolve(Method method) {
        return ResolvableType.forMethodParameter(method, 0).resolve();
    }
    
    public static class StringList extends ArrayList<String> {

        /**
         * 
         */
        private static final long serialVersionUID = 5221292000345112998L;
    }
    
    public static void resolveGenerics(Class<?> clazz) {
        ResolvableType r = ResolvableType.forClass(clazz);
        
        ResolvableType s = r.getSuperType();
        
        ResolvableType[] gs = s.getGenerics();
        
        Arrays.stream(gs).map(ResolvableType::resolve).forEach(System.out::println);
    }
    
    public static <T> void useImplementionAsResolver(Class<T> base, Class<? extends T> impl) {
        ResolvableType r = ResolvableType.forClass(base, impl);
        
        Arrays.stream(r.getGenerics()).map(ResolvableType::resolve).forEach(System.out::println);
    }
    
    public interface First<T, U> {
        
    }
    
    public interface Second<M> extends First<String, M> {
        
    }
    
    public interface Third extends Second<Integer> {
        
    }
    
    public static <T, U> void show(First<T, U> list) {}

    public static void main(String[] args) throws NoSuchMethodException, SecurityException {
//        System.out.println(resolve(App.class.getMethod("process", List.class)));
//        resolveGenerics(StringList.class);
//        resolveGenerics(ArrayList.class); 
        
//        useImplementionAsResolver(First.class, Third.class);
        
        ResolvableType r1 = ResolvableType.forClass(Third.class);
        ResolvableType r2 = ResolvableType.forClass(First.class);
        
        show(new Third() {});
        
        System.out.println(r2.isAssignableFrom(r1));
    }

}
