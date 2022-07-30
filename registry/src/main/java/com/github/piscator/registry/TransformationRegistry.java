package com.github.piscator.registry;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class TransformationRegistry {
    
    private static volatile TransformationRegistry instance;

    private HashMap<String, Map<Class<?>, Object[]>> register;

    private TransformationRegistry(String name, Map<Class<?>, Object[]> transformation) {
        this.register = new HashMap<>(Map.of(name, transformation));
    }

    private TransformationRegistry() {
        this.register = new HashMap<>();
    }

    public static TransformationRegistry getInstance(String name, Map<Class<?>, Object[]> transformation) {
        TransformationRegistry result = instance;
        if (result != null) {
            result.register(name, transformation);
            return result;
        }
        synchronized(TransformationRegistry.class) {
            if (instance == null) {
                instance = new TransformationRegistry(name, transformation);
            } else {
                instance.register(name, transformation);
            }
            return instance;
        }
    }

    public static TransformationRegistry getInstance() {
        TransformationRegistry result = instance;
        if (result != null) {
            return result;
        }
        synchronized(TransformationRegistry.class) {
            if (instance == null) {
                instance = new TransformationRegistry();
            }
            return instance;
        }
    }

    public void register(String name, Map<Class<?>, Object[]> transformationClass) {
        this.register.put(name, transformationClass);
    }

}
