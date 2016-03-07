package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.*;
import com.pholser.junit.quickcheck.generator.java.math.*;
import com.pholser.junit.quickcheck.generator.java.time.*;
import com.pholser.junit.quickcheck.generator.java.util.DateGenerator;
import com.pholser.junit.quickcheck.internal.generator.ArrayGenerator;

import java.math.*;
import java.time.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author OZY on 2016.03.07.
 */
public final class Generators {

    private Generators() {
        //hide constructor
    }

    private final static Map<Class<?>, Generator<?>> CLASS_GENERATORS = new ConcurrentHashMap<>(75);
    private final static Map<String, Generator<?>> GENERIC_CLASS_GENERATORS = new ConcurrentHashMap<>(50);
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 <>?;':[]{}-_=+|!@#$%^&*()~.,/";

    public static void registerGenerator(Class<?> aClass, Generator<?> generator) {
        CLASS_GENERATORS.put(aClass, generator);
    }

    public static void registerGenerator(String genericTypeName, Generator<?> generator) {
        GENERIC_CLASS_GENERATORS.put(genericTypeName, generator);
    }

    public static Generator<?> computeIfAbsent(Class<?> aClass, Function<Class<?>, Generator<?>> mappingFunction) {
        return CLASS_GENERATORS.computeIfAbsent(aClass, mappingFunction);
    }

    public static Generator<?> computeGenericIfAbsent(String genericTypeName, Function<String, Generator<?>> mappingFunction) {
        return GENERIC_CLASS_GENERATORS.computeIfAbsent(genericTypeName, mappingFunction);
    }

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
        // arrays
        CLASS_GENERATORS.put(Integer[].class, new ArrayGenerator(Integer.class, new IntegerGenerator()));
        CLASS_GENERATORS.put(int[].class, new ArrayGenerator(Integer.TYPE, new IntegerGenerator()));
        CLASS_GENERATORS.put(Long[].class, new ArrayGenerator(Long.class, new LongGenerator()));
        CLASS_GENERATORS.put(long[].class, new ArrayGenerator(Long.TYPE, new LongGenerator()));
        CLASS_GENERATORS.put(byte[].class, new ArrayGenerator(Byte.TYPE, new ByteGenerator()));
        CLASS_GENERATORS.put(Byte[].class, new ArrayGenerator(Byte.class, new ByteGenerator()));
        CLASS_GENERATORS.put(Double[].class, new ArrayGenerator(Double.class, new DoubleGenerator()));
        CLASS_GENERATORS.put(double[].class, new ArrayGenerator(Double.TYPE, new DoubleGenerator()));
        CLASS_GENERATORS.put(Short[].class, new ArrayGenerator(Short.class, new ShortGenerator()));
        CLASS_GENERATORS.put(short[].class, new ArrayGenerator(Short.TYPE, new ShortGenerator()));
        CLASS_GENERATORS.put(Float[].class, new ArrayGenerator(Float.class, new FloatGenerator()));
        CLASS_GENERATORS.put(float[].class, new ArrayGenerator(Float.TYPE, new FloatGenerator()));
        CLASS_GENERATORS.put(Boolean[].class, new ArrayGenerator(Boolean.class, new BooleanGenerator()));
        CLASS_GENERATORS.put(boolean[].class, new ArrayGenerator(Boolean.TYPE, new BooleanGenerator()));
        CLASS_GENERATORS.put(Character[].class, new ArrayGenerator(Character.class, new CharacterGenerator()));
        CLASS_GENERATORS.put(char[].class, new ArrayGenerator(Character.TYPE, new CharacterGenerator()));
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
}
