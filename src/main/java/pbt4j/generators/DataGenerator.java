package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import jdk.nashorn.api.scripting.*;

import javax.script.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Linas on 2016.03.05.
 */
public class DataGenerator extends Generator<Object> {

    private final static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private final List<String> values;
    private final Type typ;
    private int current = 0;


    public DataGenerator(Type typ, String[] values) {
        super(Object.class);
        this.typ = typ;
        this.values = Arrays.asList(values);
    }

    public static Object evalJs(String jsScript) {
        try {
            return engine.eval(jsScript);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object generate(SourceOfRandomness random, GenerationStatus status) {
        final Object data = evalJs(values.get(current));

        Object result = null;
        if (typ instanceof Class<?>) {
            Class<?> aClass = (Class<?>) typ;
            if (aClass.isAssignableFrom(data.getClass())) {
                result = data;
            } else if (data instanceof ScriptObjectMirror) {
                result = convertFromMirror(aClass, (ScriptObjectMirror) data);
            } else {
                result = ScriptUtils.convert(data, aClass);
            }
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) typ;
            final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (data instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) data;
                result = convertFromMirror(rawType, mirror);
            } else {
                result = ScriptUtils.convert(data, rawType);
            }

        }

        current++;
        return result;
    }

    protected Object convertFromMirror(Class<?> toClass, ScriptObjectMirror fromMirror) {
        if (toClass.isAssignableFrom(List.class)) {
            return fromMirror.values().stream()
                    .collect(Collectors.toList());
        } else if (toClass.isAssignableFrom(Map.class)) {
            return fromMirror.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else if (toClass.isAssignableFrom(Set.class)) {
            return fromMirror.values().stream().collect(Collectors.toSet());
        } else if (toClass.isArray()) {
            return fromMirror.values().toArray();
        } else throw new RuntimeException("Unsupported type: " + toClass);
    }
}
