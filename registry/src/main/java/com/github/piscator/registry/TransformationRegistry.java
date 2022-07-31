package com.github.piscator.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.piscator.exceptions.ConstructionException;
import com.github.piscator.exceptions.ConstructorNotFound;

import lombok.Getter;
/***
 * Singleton Registry implementation
 */
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

    /**
     * Get instance of TransformationRegistry, Registry of named Transformations with Class information and Arguments needed to 
     * create instantiate given Class<?> 
     * @param name of the Transformation must be unique
     * @param transformation Map<Class<?>, Object[]> 
     * @return THE instance of TransformationRegistry
     */
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

    public void register(String name, Map<Class<?>, Object[]> transformation) {
        this.register.put(name, transformation);
    }

    public Map<Class<?>, Object[]> get(String name) {
        return this.register.get(name);
    }

    public <T> T getTransformation(String name)  {
        Map<Class<?>, Object[]> trafo = get(name);
        for (Class<?> key : trafo.keySet()) {
            Object[] parameters = trafo.get(key);
            Class<?>[] parameterTypes = new Class<?>[parameters.length];
            for (int j = 0; j < parameterTypes.length; j++) {
                parameterTypes[j] = parameters[j].getClass();
            }
            try {
                Constructor<T> constructor = (Constructor<T>) key.getConstructor(parameterTypes);
                try {
                    return constructor.newInstance(parameters);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new ConstructionException(String.format("Object could not be constructed with %s!", constructor));
                }
            } catch (NoSuchMethodException e) {
                throw new ConstructorNotFound(
                    String.format("No Constructor found for Class: %s with input parameters %s", key, Arrays.toString(parameterTypes))
                    );
            } 

        }
        return null;
    }
}
