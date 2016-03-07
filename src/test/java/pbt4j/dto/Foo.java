package pbt4j.dto;

import java.util.List;
import java.util.Optional;

/**
 * @author Linas on 2016.03.06.
 */
public class Foo {

    public final String name;
    public final int age;
    public final List<Bean> beans;
    public final Optional<String> midName;

    public Foo(String name, int age, List<Bean> beans, Optional<String> midName) {
        this.name = name;
        this.age = age;
        this.beans = beans;
        this.midName = midName;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", beans=" + beans +
                ", midName=" + midName +
                '}';
    }
}
