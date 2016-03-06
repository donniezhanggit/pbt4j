package pbt4j;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.generator.java.lang.*;
import com.pholser.junit.quickcheck.generator.java.math.*;
import com.pholser.junit.quickcheck.generator.java.time.*;
import com.pholser.junit.quickcheck.generator.java.util.*;
import com.pholser.junit.quickcheck.internal.generator.*;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import pbt4j.annotations.*;
import org.junit.runners.model.*;
import pbt4j.generators.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * @author Linas on 2016.03.05.
 */
public class ParametrizedStatement extends Statement {
    private final FrameworkMethod method;
    private final Object target;
    private final List<Generator<?>> generators;
    private int times = 100;
    private final static Map<String, Generator<?>> DEFAULT_GENERATORS = new HashMap<>(50);
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 <>?;':[]{}-_=+|!@#$%^&*()~.,/";
    static {
        DEFAULT_GENERATORS.put("int", new IntegerGenerator());
        DEFAULT_GENERATORS.put("Integer", new IntegerGenerator());
        DEFAULT_GENERATORS.put("String", new DefaultStringGenerator(RANDOM_CHARS));
        DEFAULT_GENERATORS.put("Long", new LongGenerator());
        DEFAULT_GENERATORS.put("long", new LongGenerator());
        DEFAULT_GENERATORS.put("Double", new DoubleGenerator());
        DEFAULT_GENERATORS.put("double", new DoubleGenerator());
        DEFAULT_GENERATORS.put("Byte", new ByteGenerator());
        DEFAULT_GENERATORS.put("byte", new ByteGenerator());
        DEFAULT_GENERATORS.put("Short", new ShortGenerator());
        DEFAULT_GENERATORS.put("short", new ShortGenerator());
        DEFAULT_GENERATORS.put("Float", new FloatGenerator());
        DEFAULT_GENERATORS.put("float", new FloatGenerator());
        DEFAULT_GENERATORS.put("Boolean", new BooleanGenerator());
        DEFAULT_GENERATORS.put("boolean", new BooleanGenerator());
        DEFAULT_GENERATORS.put("Character", new CharacterGenerator());
        DEFAULT_GENERATORS.put("char", new CharacterGenerator());
        DEFAULT_GENERATORS.put("BigInteger", new BigIntegerGenerator());
        DEFAULT_GENERATORS.put("Duration", new DurationGenerator());
        DEFAULT_GENERATORS.put("Instant", new InstantGenerator());
        DEFAULT_GENERATORS.put("LocalDate", new LocalDateGenerator());
        DEFAULT_GENERATORS.put("LocalDateTime", new LocalDateTimeGenerator());
        DEFAULT_GENERATORS.put("LocalTime", new LocalTimeGenerator());
        DEFAULT_GENERATORS.put("MonthDay", new MonthDayGenerator());
        DEFAULT_GENERATORS.put("OffsetDateTime", new OffsetDateTimeGenerator());
        DEFAULT_GENERATORS.put("OffsetTime", new OffsetTimeGenerator());
        DEFAULT_GENERATORS.put("Period", new PeriodGenerator());
        DEFAULT_GENERATORS.put("Year", new YearGenerator());
        DEFAULT_GENERATORS.put("YearMonth", new YearMonthGenerator());
        DEFAULT_GENERATORS.put("ZonedDateTime", new ZonedDateTimeGenerator());
        DEFAULT_GENERATORS.put("ZoneOffset", new ZoneOffsetGenerator());
        DEFAULT_GENERATORS.put("Date", new DateGenerator());
    }

    public ParametrizedStatement(FrameworkMethod method, Object target) {
        this.method = method;
        this.target = target;
        this.generators = resolveGenerators(method);
    }

    class Pair {
        Type type;
        JsData jsData;

        public Pair(Type type, JsData jsData) {
            this.type = type;
            this.jsData = jsData;
        }
    }

    protected List<Generator<?>> resolveGenerators(FrameworkMethod method) {
        final List<JsData> dataFromAnnotations = Stream.of(method.getMethod().getParameterAnnotations())
                .flatMap(Stream::of)
                .filter(annotation -> annotation instanceof JsData)
                .map(annotation -> (JsData) annotation)
                .collect(Collectors.toList());

        final List<Type> parameterTypes = Stream.of(method.getMethod().getGenericParameterTypes())
                .collect(Collectors.toList());

        if (!dataFromAnnotations.isEmpty()) {
            final long count = dataFromAnnotations.stream()
                    .map(testData -> testData.value().length)
                    .distinct()
                    .count();
            if (count > 1) {
                throw new IllegalArgumentException("@JsData arguments is of different size");
            }

            times = dataFromAnnotations.get(0).value().length;

            return IntStream.range(0, dataFromAnnotations.size())
                    .mapToObj(i -> new Pair(parameterTypes.get(i), dataFromAnnotations.get(i)))
                    .map(pair -> new DataGenerator(pair.type, pair.jsData.value()))
                    .collect(Collectors.toList());
        }

        times = Optional.ofNullable(method.getAnnotation(Repeat.class))
                .map(Repeat::times)
                .orElse(times);

        return parameterTypes.stream()
                .map(this::resolveGenerator)
                .collect(Collectors.toList());
    }

    protected Generator<?> resolveGenerator(Type type) {
        if (type instanceof Class<?>) {
            Class<?> aClass = (Class<?>) type;
            if (aClass.isEnum()) {
                return new EnumGenerator(aClass);
            }

            Generator<?> generator = DEFAULT_GENERATORS.get(aClass.getSimpleName());
            if (generator == null) {
                generator = new ClassGenerator(aClass, this::resolveGenerator, this::getStatus);
                //throw new UnsupportedOperationException("Generator not available for type: " + type.getTypeName());
            }

            return generator;
        } else {
            return DEFAULT_GENERATORS.computeIfAbsent(type.getTypeName(),
                    key -> resolveGenericTypeGenerator(type));
        }
    }

    private Generator<?> resolveGenericTypeGenerator(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        final List<Generator<?>> components = Stream.of(parameterizedType.getActualTypeArguments())
                .map(this::resolveGenerator)
                .collect(Collectors.toList());

        //resolve root generator
        Generator<?> rootGenerator = null;
        final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        if (rawType.isAssignableFrom(Optional.class)) {
            rootGenerator = new OptionalGenerator();
        } else if (rawType.isAssignableFrom(List.class)) {
            rootGenerator = new ArrayListGenerator();
        } else if (rawType.isAssignableFrom(Map.class)) {
            rootGenerator = new HashMapGenerator();
        } else if (rawType.isAssignableFrom(Set.class)) {
            rootGenerator = new HashSetGenerator();
        }

        if (!components.isEmpty() && rootGenerator != null) {
            rootGenerator.addComponentGenerators(components);
        }

        if (rootGenerator == null) {
            throw new UnsupportedOperationException("Generator not available for type: " + type.getTypeName());
        }
        return rootGenerator;
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
