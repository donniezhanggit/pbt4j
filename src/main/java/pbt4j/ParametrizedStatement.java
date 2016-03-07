package pbt4j;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.internal.generator.*;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import pbt4j.annotations.*;
import org.junit.runners.model.*;
import pbt4j.generators.*;

import javax.script.ScriptContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Linas on 2016.03.05.
 */
public class ParametrizedStatement extends Statement {
    private final FrameworkMethod method;
    private final Object target;
    private final ScriptContext scriptContext;
    private final List<Generator<?>> generators;
    private int times = 100;

    public ParametrizedStatement(FrameworkMethod method, Object target, ScriptContext scriptContext) {
        this.method = method;
        this.target = target;
        this.scriptContext = scriptContext;
        this.generators = resolveGenerators(method);
    }

    class Pair {
        Type typ;
        String[] jsData;

        public Pair(Type typ, String[] jsData) {
            this.typ = typ;
            this.jsData = jsData;
        }
    }

    protected List<Generator<?>> resolveGenerators(FrameworkMethod method) {
        final List<String[]> dataFromAnnotations = Stream.of(method.getMethod().getParameterAnnotations())
                .flatMap(Stream::of)
                .flatMap(this::jsonValues)
                .collect(Collectors.toList());

        final List<Type> parameterTypes = Stream.of(method.getMethod().getGenericParameterTypes())
                .collect(Collectors.toList());

        if (!dataFromAnnotations.isEmpty()) {
            final long count = dataFromAnnotations.stream()
                    .map(testData -> testData.length)
                    .distinct()
                    .count();
            if (count > 1) {
                throw new IllegalArgumentException("@JsData or @JsonData arguments are of different size");
            }

            times = dataFromAnnotations.get(0).length;

            return IntStream.range(0, dataFromAnnotations.size())
                    .mapToObj(i -> new Pair(parameterTypes.get(i), dataFromAnnotations.get(i)))
                    .map(pair -> new JsDataGenerator(pair.typ, pair.jsData, scriptContext))
                    .collect(Collectors.toList());
        }

        times = Optional.ofNullable(method.getAnnotation(Repeat.class))
                .map(Repeat::times)
                .orElse(times);

        return parameterTypes.stream()
                .map(this::resolveGenerator)
                .collect(Collectors.toList());
    }

    protected Stream<String[]> jsonValues(Annotation annotation) {
        if (annotation instanceof JsData) {
            JsData jsData = (JsData) annotation;
            return Stream.<String[]>of(jsData.value());
        } else if (annotation instanceof JsonData) {
            JsonData jsonData = (JsonData) annotation;
            final String[] values = Stream.of(jsonData.value())
                    .map(script -> "Java.asJSONCompatible(" + script + ")")
                    .toArray(String[]::new);
            return Stream.<String[]>of(values);
        }
        return Stream.empty();
    }

    protected Generator<?> resolveGenerator(Type typ) {
        if (typ instanceof Class<?>) {
            Class<?> aClass = (Class<?>) typ;
            if (aClass.isEnum()) {
                return new EnumGenerator(aClass);
            }
            return Generators.findGenerator(aClass)
                    .orElseGet(() -> new ClassGenerator(aClass, this::resolveGenerator, this::getStatus));
        } else {
            return resolveGenericTypeGenerator(typ);
        }
    }

    private Generator<?> resolveGenericTypeGenerator(Type typ) {
        ParameterizedType parameterizedType = (ParameterizedType)typ;
        final List<Generator<?>> components = Stream.of(parameterizedType.getActualTypeArguments())
                .map(this::resolveGenerator)
                .collect(Collectors.toList());

        final Class<?> rawTypeOfGenericClass = (Class<?>) parameterizedType.getRawType();
        return resolveGeneratorFromGenericClass(components, rawTypeOfGenericClass, parameterizedType);
    }

    private Generator<?> resolveGeneratorFromGenericClass(List<Generator<?>> components, Class<?> rawTypeOfGenericClass,
                                                          ParameterizedType parameterizedType) {
        return Generators.findGenerator(rawTypeOfGenericClass)
                .map(generator -> {
                    if (!components.isEmpty()) generator.addComponentGenerators(components);
                    return generator;
                })
                .orElseThrow(() -> new UnsupportedOperationException("Generator not available for type: " + parameterizedType.getTypeName()));
    }

    @Override
    public void evaluate() throws Throwable {
        for (int i = 0; i < times; i++) {
            final Object[] randomArgs = generators.stream()
                    .map(generator -> generator.generate(new SourceOfRandomness(new Random()), getStatus()))
                    .toArray();
            method.invokeExplosively(target, randomArgs);
        }
    }

    private GenerationStatus getStatus() {
        return new GenerationStatus() {
            @Override
            public int size() {
                return Math.max(0, new Random().nextInt(100));
            }

            @Override
            public int attempts() {
                return 1;
            }
        };
    }
}
