package com.github.piscator.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.piscator.exceptions.ConstructorNotFound;

public class TransformationRegistryTest {


    class TestClass {
        String testClassName;
        Integer number;
        public TestClass(String name){
            this.testClassName = name;
        }
        public TestClass(String name, Integer number) {
            this.testClassName = name;
            this.number = number;
        }
    }

    static class ThreadFoo implements Runnable {
        TransformationRegistry registry;

        @Override
        public void run() {
            Object[] in = {"test", 1}; 
            this.registry = TransformationRegistry.getInstance("testFoo", Map.of(TestClass.class, in));
        }
        
    }

    static class ThreadBar implements Runnable {

        TransformationRegistry registry;
        @Override
        public void run() {
            Object[] in = {"bar", 2}; 
            this.registry = TransformationRegistry.getInstance("testBar", Map.of(TestClass.class, in));
        }

    
    }

    @Test
    void testGetInstance() {

        Object[] inputs = {"stering", 1};
        Map<Class<?>, Object[]> transformation = Map.of(TestClass.class, inputs);
        TransformationRegistry registry = TransformationRegistry.getInstance("test", transformation);
        assertEquals(transformation, registry.getRegister().get("test"));

    }

    @Test
    void testRegister() {
        ThreadFoo foo = new ThreadFoo();
        Thread thread1 = new Thread(foo);
        ThreadBar bar = new ThreadBar();
        Thread thread2 = new Thread(bar);
        thread1.start();
        thread2.start();
        assertEquals(foo.registry, bar.registry);
        Object[] inputs = {"bar", 2};
        
        assertEquals(foo.registry.getRegister().get("testBar").get(TestClass.class)[0], inputs[0]);
        assertEquals(foo.registry.getRegister().get("testBar").get(TestClass.class)[1], inputs[1]);


    }

    @Test
    void testGet() {
        TransformationRegistry registry = TransformationRegistry.getInstance();
        Object[] inputs = {"stering", 1};
        Map<Class<?>, Object[]> transformation = Map.of(TestClass.class, inputs);
        registry = TransformationRegistry.getInstance("test", transformation);
        assertEquals(inputs, registry.get("test").get(TestClass.class));
    }


    @Test
    void testGetTransformation() {
        Object[] inputs = {"stering", 1};
        Map<Class<?>, Object[]> transformation = Map.of(SimpleTestClass.class, inputs);
        TransformationRegistry registry = TransformationRegistry.getInstance("test", transformation);
        SimpleTestClass testClassInstance = registry.getTransformation("test");
        assertEquals(SimpleTestClass.class, testClassInstance.getClass());
    }

    @Test
    void testExceptionThrown() {
        Object[] inputs = {"stering", 1};
        Map<Class<?>, Object[]> transformation = Map.of(TestClass.class, inputs);
        TransformationRegistry registry = TransformationRegistry.getInstance("test", transformation);
        assertThrows(ConstructorNotFound.class, () -> registry.getTransformation("test"));
        Object[] in = {1};
        registry.register("test2", Map.of(SimpleTestClass.class, in));

    }

}
