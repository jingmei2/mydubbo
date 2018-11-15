package com.magi.api;

public class MjHelloImpl implements IMjHello{
    @Override
    public String sayHello(String name) {
        return "I'm "+ name;
    }
}
