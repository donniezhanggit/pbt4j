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

import javax.script.ScriptContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final static Map<Class<?>, Generator<?>> CLASS_GENERATORS = new ConcurrentHashMap<>(50);
    private final static Map<String, Generator<?>> GENERIC_CLASS_GENERATORS = new ConcurrentHashMap<>(50);
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 <>?;':[]{}-_=+|!@#$%^&*()~.,/";
    static {
        //primitives
        CLASS_GENERATORS.put(Integer.TYPE, new IntegerGenerator());
        CLASS_GENERATORS.put(Long.TYPE, new LongGenerator());
        CLASS_GENERATORS.put(Double.TYPE, new DoubleGenerator());
        CLASS_GENERATORS.put(Byte.TYPE, new ByteGenerator());
        CLASS_GENERATORS.put(Short.TYPE, new ShortGenerator());
        CLASS_GENERATORS.put(Float.TYPE, new FloatGenerator());
        CLASS_GENERATORS.put(Boolean.TYPE, new BooleanGenerator());
        CLASS_GENERATORS.put(Character.TYPE, new CharacterGenerator());
        //classes
        CLASS_GENERATORS.put(Integer.class, new IntegerGenerator());
        CLASS_GENERATORS.put(String.class, new DefaultStringGenerator(RANDOM_CHARS));
        CLASS_GENERATORS.put(Long.class, new LongGenerator());
        CLASS_GENERATORS.put(Double.class, new DoubleGenerator());
        CLASS_GENERATORS.put(Byte.class, new ByteGenerator());
        CLASS_GENERATORS.put(Short.class, new ShortGenerator());
        CLASS_GENERATORS.put(Float.class, new FloatGenerator());
        CLASS_GENERATORS.put(Boolean.class, new BooleanGenerator());
        CLASS_GENERATORS.put(Character.class, new CharacterGenerator());
        CLASS_GENERATORS.put(BigInteger.class, new BigIntegerGenerator());
        CLASS_GENERATORS.put(BigDecimal.class, new BigDecimalGenerator());
        CLASS_GENERATORS.put(Duration.class, new DurationGenerator());
        CLASS_GENERATORS.put(LocalDate.class, new LocalDateGenerator());
        CLASS_GENERATORS.put(LocalDateTime.class, new LocalDateTimeGenerator());
        CLASS_GENERATORS.put(LocalTime.class, new LocalTimeGenerator());
        CLASS_GENERATORS.put(MonthDay.class, new MonthDayGenerator());
        CLASS_GENERATORS.put(OffsetDateTime.class, new OffsetDateTimeGenerator());
        CLASS_GENERATORS.put(OffsetTime.class, new OffsetTimeGenerator());
        CLASS_GENERATORS.put(Period.class, new PeriodGenerator());
        CLASS_GENERATORS.put(Year.class, new YearGenerator());
        CLASS_GENERATORS.put(YearMonth.class, new YearMonthGenerator());
        CLASS_GENERATORS.put(ZonedDateTime.class, new ZonedDateTimeGenerator());
        CLASS_GENERATORS.put(java.util.Date.class, new DateGenerator());
    }

    public ParametrizedStatement(FrameworkMethod method, Object target, ScriptContext scriptContext) {
        this.method = method;
        this.target = target;
        this.scriptContext = scriptContext;
        this.generators = resolveGenerators(method);
    }

    public static void registerGenerator(Class<?> aClass, Generator<?> generator) {
        CLASS_GENERATORS.put(aClass, generator);
    }

    public static void registerGenerator(String genericTypeName, Generator<?> generator) {
        GENERIC_CLASS_GENERATORS.put(genericTypeName, generator);
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
                    .toArray(size -> new String[size]);
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
            return CLASS_GENERATORS.computeIfAbsent(aClass, classz ->
                new ClassGenerator(classz, this::resolveGenerator, this::getStatus));
        } else {
            return GENERIC_CLASS_GENERATORS.computeIfAbsent(typ.getTypeName(),
                    key -> resolveGenericTypeGenerator(typ));
        }
    }

    private Generator<?> resolveGenericTypeGenerator(Type typ) {
        ParameterizedType parameterizedType = (ParameterizedType)typ;
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
            throw new UnsupportedOperationException("Generator not available for type: " + typ.getTypeName());
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
