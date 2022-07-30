package com.github.piscator.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.Map;

import org.junit.jupiter.api.Test;

public class TransformationRegistryTest {


    class TestClass {
        String testClassName;
        public TestClass(String name){
            this.testClassName = name;
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
        foo.run();
        ThreadBar bar = new ThreadBar();
        bar.run();
        assertEquals(foo.registry, bar.registry);
        

    }
}
