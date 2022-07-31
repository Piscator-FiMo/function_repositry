package com.github.piscator.registry;

public class SimpleTestClass {
    String testClassName;
    Integer number;

    public SimpleTestClass(String name) {
        this.testClassName = name;
    }

    public SimpleTestClass(String name, Integer number) {
        this.testClassName = name;
        this.number = number;
    }

    private SimpleTestClass(Integer number) {

    }
}
