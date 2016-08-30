package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.reflections.Reflections;

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
    private final Class<?> aClass;
    private final Reflections reflections;

    public ClassGenerator(Class<?> aClass, Function<Type, Generator<?>> generatorFunction, Supplier<GenerationStatus> statusSupplier) {
        super(Object.class);
        this.generatorFunction = generatorFunction;
        this.statusSupplier = statusSupplier;
        this.aClass = aClass;
        this.reflections = new Reflections(aClass.getPackage().getName());
    }

    @Override
    public Object generate(SourceOfRandomness random, GenerationStatus status) {
        final Constructor<?> constructor = Stream.of(resolveConstructors(aClass, random))
                .max((o1, o2) -> Integer.valueOf(o1.getParameterCount()).compareTo(o2.getParameterCount()))
                .orElseThrow(() -> new IllegalAccessError("Cannot find constructor for type: " + aClass));
        constructor.setAccessible(true);
        final List<Object> generatedArgs = Stream.of(constructor.getGenericParameterTypes())
                .map(generatorFunction)
                .map(generator -> generator.generate(new SourceOfRandomness(new Random()), statusSupplier.get()))
                .collect(Collectors.toList());

        try {
            if (generatedArgs.isEmpty()) {
                final Object bean = constructor.newInstance();
                Stream.of(bean.getClass().getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .filter(field -> !Modifier.isFinal(field.getModifiers()))
                        .peek(field -> field.setAccessible(true))
                        .forEach(field -> setRandomFieldValue(bean, field, random));
                return bean;
            }
            return constructor.newInstance(generatedArgs.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRandomFieldValue(Object o, Field field, SourceOfRandomness random) {
        final Object randomValue = Optional.ofNullable(field.getGenericType())
                .map(generatorFunction::apply)
                .map(generator -> generator.generate(random, statusSupplier.get()))
                .orElseThrow(() -> new UnsupportedOperationException("Cannot generate values for: " + o));

        try {
            field.set(o, randomValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?>[] resolveConstructors(Class<?> aClass, SourceOfRandomness random) {
        if (aClass.isInterface()) {
            final List<Class<?>> subTypes = reflections.getSubTypesOf(aClass).stream().collect(Collectors.toList());
            if (subTypes.isEmpty()) {
                throw new IllegalAccessError("Cannot find subtypes for interface type: " + aClass + ". Subtypes must be in the same package.");
            }
            return subTypes.get(random.nextInt(subTypes.size())).getDeclaredConstructors();
        }
        return aClass.getDeclaredConstructors();
    }
}
