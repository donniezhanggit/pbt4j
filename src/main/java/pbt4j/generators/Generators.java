package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.*;
import com.pholser.junit.quickcheck.generator.java.math.*;
import com.pholser.junit.quickcheck.generator.java.time.*;
import com.pholser.junit.quickcheck.generator.java.util.*;
import com.pholser.junit.quickcheck.generator.java.util.function.*;
import com.pholser.junit.quickcheck.internal.generator.ArrayGenerator;

import java.math.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * @author OZY on 2016.03.07.
 */
public final class Generators {

    private Generators() {
        //hide constructor
    }

    private final static Map<Class<?>, Generator<?>> CLASS_GENERATORS = new ConcurrentHashMap<>(100);
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 <>?;':[]{}-_=+|!@#$%^&*()~.,/";

    public static void registerGenerator(Class<?> aClass, Generator<?> generator) {
        CLASS_GENERATORS.put(aClass, generator);
    }

    public static Optional<Generator<?>> findGenerator(Class<?> aClass) {
        return Optional.ofNullable(CLASS_GENERATORS.get(aClass));
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
        CLASS_GENERATORS.put(TimeZone.class, new TimeZoneGenerator());
        CLASS_GENERATORS.put(java.util.Date.class, new DateGenerator());

        CLASS_GENERATORS.put(Optional.class, new OptionalGenerator());
        CLASS_GENERATORS.put(Stream.class, new StreamGenerator<>());
        CLASS_GENERATORS.put(List.class, new ArrayListGenerator());
        CLASS_GENERATORS.put(ArrayList.class, new ArrayListGenerator());
        CLASS_GENERATORS.put(LinkedList.class, new LinkedListGenerator());
        CLASS_GENERATORS.put(Map.class, new HashMapGenerator());
        CLASS_GENERATORS.put(HashMap.class, new HashMapGenerator());
        CLASS_GENERATORS.put(IdentityHashMap.class, new IdentityHashMapGenerator());
        CLASS_GENERATORS.put(LinkedHashMap.class, new LinkedHashMapGenerator());
        CLASS_GENERATORS.put(Hashtable.class, new HashtableGenerator());
        CLASS_GENERATORS.put(Set.class, new HashSetGenerator());
        CLASS_GENERATORS.put(HashSet.class, new HashSetGenerator());
        CLASS_GENERATORS.put(LinkedHashSet.class, new LinkedHashSetGenerator());
        CLASS_GENERATORS.put(Stack.class, new StackGenerator());
        CLASS_GENERATORS.put(Vector.class, new VectorGenerator());

        CLASS_GENERATORS.put(Supplier.class, new SupplierGenerator<>());
        CLASS_GENERATORS.put(Function.class, new FunctionGenerator<>());
        CLASS_GENERATORS.put(BiFunction.class, new BiFunctionGenerator<>());
        CLASS_GENERATORS.put(BiPredicate.class, new BiPredicateGenerator<>());
        CLASS_GENERATORS.put(DoubleFunction.class, new DoubleFunctionGenerator<>());
        CLASS_GENERATORS.put(IntFunction.class, new IntFunctionGenerator<>());
        CLASS_GENERATORS.put(LongFunction.class, new LongFunctionGenerator<>());
        CLASS_GENERATORS.put(Predicate.class, new PredicateGenerator<>());
        CLASS_GENERATORS.put(ToDoubleBiFunction.class, new ToDoubleBiFunctionGenerator<>());
        CLASS_GENERATORS.put(ToDoubleFunction.class, new ToDoubleFunctionGenerator<>());
        CLASS_GENERATORS.put(ToIntBiFunction.class, new ToIntBiFunctionGenerator<>());
        CLASS_GENERATORS.put(ToIntFunction.class, new ToIntFunctionGenerator<>());
        CLASS_GENERATORS.put(ToLongBiFunction.class, new ToLongBiFunctionGenerator<>());
        CLASS_GENERATORS.put(ToLongFunction.class, new ToLongFunctionGenerator<>());
        CLASS_GENERATORS.put(UnaryOperator.class, new UnaryOperatorGenerator<>());
        CLASS_GENERATORS.put(OptionalDouble.class, new OptionalDoubleGenerator());
        CLASS_GENERATORS.put(OptionalInt.class, new OptionalIntGenerator());
        CLASS_GENERATORS.put(OptionalLong.class, new OptionalLongGenerator());
        CLASS_GENERATORS.put(Properties.class, new PropertiesGenerator());
        CLASS_GENERATORS.put(BitSet.class, new BitSetGenerator());
    }
}
