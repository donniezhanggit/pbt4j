[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.soundvibe/pbt4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.soundvibe/pbt4j)
[![Build Status](https://travis-ci.org/soundvibe/pbt4j.png)](https://travis-ci.org/soundvibe/pbt4j)
[![Coverage Status](https://codecov.io/github/soundvibe/pbt4j/coverage.svg?branch=develop)](https://codecov.io/github/soundvibe/pbt4j?branch=develop)
[![Join the chat at https://gitter.im/soundvibe/pbt4j](https://badges.gitter.im/soundvibe/pbt4j.svg)](https://gitter.im/soundvibe/pbt4j?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# pbt4j

Property-based testing extensions for JUnit.
pbt4j allows you to add parameters to your test methods. You can either provide your parameter's values using annotations or
they will be randomly generated. You can also control how many times test are being run (default value is 100 times).
When providing custom test values, pbt4j uses Nashorn's JavaScript engine to resolve them so possibilities are almost endless.

Learn more about pbt4j on the [Wiki home](https://github.com/soundvibe/pbt4j/wiki).

## Getting started

```java
import org.junit.*;
import org.junit.runner.RunWith;
import pbt4j.PropertyBasedTesting;
import pbt4j.annotations.*;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(PropertyBasedTesting.class)
@JsEval("var Foo = Java.type('pbt4j.Foo'); var Bean = Java.type('pbt4j.Bean');")
//we can optionally register our custom types in js context if we want to provide them using @JsData annotation
public class PropertyBasedTestingTest {
    @Test
    public void shouldProvideTwoArguments(
           @JsData({"1", "2"}) long numbers,
           @JsData({"'foo'", "'bar'"}) String strings) {
        System.out.println("arg1: " + numbers);
        System.out.println("arg2: " + strings);
        //first run prints:
        // arg1: 1
        // arg2: foo
        //second run prints:
        // arg1: 2
        // arg2: bar
    }

    @Test
    public void shouldProvideListsOfArguments(
            @JsData({"['foo', 'bar']", "['second', 'third']"}) List<String> arg,
            @JsData({"[1, 2]", "[3, 4]"}) List<Integer> numbers) {

        assertEquals(2, arg.size());
        assertEquals(2, numbers.size());

        System.out.println(arg);
        System.out.println(numbers);
        //first run prints:
        // [foo, bar]
        // [1, 2]
        //second run prints:
        // [second, third]
        // [3, 4]
    }

    @Test
    @Repeat(times = 10)
    public void shouldProvideRandomValuesAndCallMethod10Times(int number) throws Exception {
        System.out.println(number);
        //Prints 10 random numbers
    }

    @Test
    public void shouldProvideCustomClass(Foo foo) throws Exception {
        System.out.println(foo);
        //prints randomly generated Foo 100 times
    }

    @Test
    public void shouldProvideMap(
            @JsData({"Java.asJSONCompatible( {foo: 'bar', second: 'bar2'})"}) Map<String, String> map) throws Exception {
        assertEquals(HashMap.class, map.getClass());
        assertEquals("bar", map.get("foo"));
        assertEquals("bar2", map.get("second"));
    }

    @Test
        public void shouldProvideCustomClassUsingJs(
                @JsData({"new Foo('Foo', 28, [new Bean()], null)"}) Foo foo) throws Exception {
            assertEquals("Foo", foo.name);
            assertEquals(28, foo.age);
            assertEquals(Foo.class, foo.getClass());
        }

}
```

## Binaries

Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cnet.soundvibe.reacto).

Example for Gradle:

```groovy
compile 'net.soundvibe:pbt4j:x.y.z'
```

and for Maven:

```xml
<dependency>
    <groupId>net.soundvibe</groupId>
    <artifactId>pbt4j</artifactId>
    <version>x.y.z</version>
</dependency>
```


## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/soundvibe/pbt4j/issues).

## LICENSE

Copyright 2016 Linas Naginionis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

