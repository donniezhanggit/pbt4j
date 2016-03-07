package pbt4j;

import org.junit.*;
import org.junit.runner.RunWith;
import pbt4j.annotations.*;
import pbt4j.dto.Bean;
import pbt4j.dto.Foo;
import pbt4j.dto.SimpleDto;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Linas on 2016.03.05.
 */
@RunWith(PropertyBasedTesting.class)
@JsEval("var Foo = Java.type('pbt4j.dto.Foo'); var Bean = Java.type('pbt4j.dto.Bean'); var SimpleDto = Java.type('pbt4j.dto.SimpleDto');")
public class PropertyBasedTestingTest {

    @Test
    public void shouldProvideTwoArguments(
           @JsonData({"1", "2"}) long numbers,
           @JsonData({"'foo'", "'bar'"}) String strings) {
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
    public void shouldProvideMap( //this works only from JDK 8u60+
            @JsonData({"{foo: 'bar', second: 'bar2'}"}) Map<String, String> map
            ) throws Exception {
        assertEquals(HashMap.class, map.getClass());
        assertEquals("bar", map.get("foo"));
        assertEquals("bar2", map.get("second"));
        assertEquals(2, map.size());
    }

    @Test
    public void withoutParameters() throws Exception {
        assertTrue(true);
        //if test method does not contain parameters, just use junit default runner
    }

    @Test
    @Repeat(times = 1)
    public void shouldProvideCustomClass(Foo foo) throws Exception {
        assertNotNull(foo);
    }

    @Test
    @Repeat(times = 1)
    //@Named("should Provide Java bean")
    public void shouldProvideJavaBean(Bean bean) throws Exception {
        assertNotNull(bean);
    }

    @Test
    public void shouldProvideCustomClassUsingJs(
            @JsData({"new Foo('Foo', 28, [new Bean()], null)"}) Foo foo) throws Exception {
        assertEquals("Foo", foo.name);
        assertEquals(28, foo.age);
        assertEquals(Foo.class, foo.getClass());
        assertEquals(1, foo.beans.size());
    }

    @Test
    public void shouldProvideSimpleDto(
            @JsData({"new SimpleDto('test', 100, ['Alice', 'Bob'])", "new SimpleDto('test2', 100, ['Alice', 'Bob'])"})
            SimpleDto simpleDto
    ) throws Exception {
        assertTrue(simpleDto.name.startsWith("test"));
        assertEquals(100, simpleDto.total);
        assertEquals("Alice", simpleDto.neighbours.get(0));
        assertEquals("Bob", simpleDto.neighbours.get(1));
        assertEquals(2, simpleDto.neighbours.size());
    }
}
