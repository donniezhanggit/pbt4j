package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Linas on 2016.03.06.
 */
public class ClassGenerator extends Generator<Object> {

    private final Function<Type, Generator<?>> generatorFunction;
    private final Supplier<GenerationStatus> statusSupplier;
    private final Constructor<?> constructor;

    public ClassGenerator(Class<?> type, Function<Type, Generator<?>> generatorFunction, Supplier<GenerationStatus> statusSupplier) {
        super(Object.class);
        this.generatorFunction = generatorFunction;
        this.statusSupplier = statusSupplier;
        this.constructor = Stream.of(type.getConstructors())
                .max((o1, o2) -> Integer.valueOf(o1.getParameterCount()).compareTo(o2.getParameterCount()))
                .orElseThrow(() -> new IllegalAccessError("Cannot find constructor for type: " + type.getTypeName()));
    }

    @Override
    public Object generate(SourceOfRandomness random, GenerationStatus status) {
        final List<?> generatedArgs = Stream.of(constructor.getGenericParameterTypes())
                .map(generatorFunction)
                .map(generator -> generator.generate(new SourceOfRandomness(new Random()), statusSupplier.get()))
                .collect(Collectors.toList());

        try {
            if (generatedArgs.isEmpty()) {
                final Object bean = constructor.newInstance();
                Stream.of(bean.getClass().getDeclaredFields())
                        .peek(field -> field.setAccessible(true))
                        .forEach(field -> setRandomFieldValue(bean, field));
                return bean;
            }
            return constructor.newInstance(generatedArgs.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setRandomFieldValue(Object o, Field field) {
        final Object randomValue = Optional.ofNullable(field.getGenericType())
                .map(generatorFunction::apply)
                .map(generator -> generator.generate(new SourceOfRandomness(new Random()), statusSupplier.get()))
                .orElseThrow(() -> new UnsupportedOperationException("Cannot generate values for: " + o));

        try {
            field.set(o, randomValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
