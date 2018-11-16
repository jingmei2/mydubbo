package com.magi.api;

import com.magi.annotation.RpcAnnotation;

@RpcAnnotation(IMjHello.class)
public class MjHelloImpl implements IMjHello{
    @Override
    public String sayHello(String name) {
        return "I'm "+ name;
    }
}
