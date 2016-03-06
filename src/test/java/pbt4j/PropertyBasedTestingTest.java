package pbt4j;

import org.junit.*;
import org.junit.runner.RunWith;
import pbt4j.annotations.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Linas on 2016.03.05.
 */
@RunWith(PropertyBasedTesting.class)
@JsEval("var Foo = Java.type('pbt4j.Foo'); var Bean = Java.type('pbt4j.Bean');")
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
    public void shouldRunTestMethod100Times(String text) throws Exception {
        System.out.println(text);
        //prints random text 100 times
    }

    @Test
    @Repeat(times = 10)
    public void shouldProvideRandomValuesAndCallMethod10Times(int number) throws Exception {
        System.out.println(number);
        //Prints 10 random numbers
    }

    @Test
    public void shouldProvideMap(
            @JsData({"Java.asJSONCompatible( {foo: 'bar', second: 'bar2'})"}) Map<String, String> map
            ) throws Exception {
        assertEquals(HashMap.class, map.getClass());
        assertEquals("bar", map.get("foo"));
        assertEquals("bar2", map.get("second"));
        System.out.println(map);
    }

    @Test
    public void withoutParameters() throws Exception {
        System.out.println("1");
        //if test method does not contain parameters, just use junit default runner
    }

    @Test
    @Repeat(times = 1)
    public void shouldProvideCustomClass(Foo foo) throws Exception {
        System.out.println(foo);
    }

    @Test
    @Repeat(times = 1)
    //@Named("should Provide Java bean")
    public void shouldProvideJavaBean(Bean bean) throws Exception {
        System.out.println(bean);
    }

    @Test
    public void shouldProvideCustomClassUsingJs(
            @JsData({"new Foo('Foo', 28, [new Bean()], null)"}) Foo foo) throws Exception {
        assertEquals("Foo", foo.name);
        assertEquals(28, foo.age);
        assertEquals(Foo.class, foo.getClass());
        System.out.println(foo);
    }
}
