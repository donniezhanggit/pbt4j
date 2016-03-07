package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.generator.java.util.ArrayListGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author Linas on 2016.03.07.
 */
public class StreamGenerator<T> extends Generator<Stream<T>> {

    private final ArrayListGenerator listGenerator;

    public StreamGenerator(Class<Stream<T>> type, List<Generator<?>> components) {
        super(type);
        this.listGenerator = new ArrayListGenerator();
        this.listGenerator.addComponentGenerators(components);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<T> generate(SourceOfRandomness random, GenerationStatus status) {
        final ArrayList<T> generate = listGenerator.generate(random, status);
        return generate.stream();
    }
}
