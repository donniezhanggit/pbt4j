package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.generator.java.util.ArrayListGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author Linas on 2016.03.07.
 */
public class StreamGenerator<T> extends ComponentizedGenerator<Stream<T>> {

    private final ArrayListGenerator listGenerator = new ArrayListGenerator();

    public StreamGenerator() {
        super(null);
    }

    @Override
    public void addComponentGenerators(List<Generator<?>> newComponents) {
        listGenerator.addComponentGenerators(newComponents);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<T> generate(SourceOfRandomness random, GenerationStatus status) {
        final ArrayList<T> generate = listGenerator.generate(random, status);
        return generate.stream();
    }
}
