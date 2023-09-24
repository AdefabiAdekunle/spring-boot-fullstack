package com.adekunle;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class FooService {

    //Since we already annotate the method that create an object of Foo
    //Then we can inject in our SpringBoot application
    private final Main.Foo foo;

    public FooService(Main.Foo foo) {
        this.foo = foo;
    }

    public String getFooName() {
        return foo.name();
    }
}
