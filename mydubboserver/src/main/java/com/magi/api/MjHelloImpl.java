package com.magi.api;

import com.magi.annotation.RpcAnnotation;

/**
 * @author magi
 */
@RpcAnnotation(IMjHello.class)
public class MjHelloImpl implements IMjHello{
    @Override
    public String sayHello(String name) {
        return "I'm "+ name;
    }
}
