package pbt4j.generators;

import com.pholser.junit.quickcheck.generator.java.lang.AbstractStringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

/**
 * @author Linas on 2016.03.06.
 */
public class DefaultStringGenerator extends AbstractStringGenerator {

    private final String characters;

    public DefaultStringGenerator(String characters) {
        this.characters = characters;
    }

    @Override
    protected int nextCodePoint(SourceOfRandomness random) {
        return characters.codePointAt(random.nextInt(characters.length()));
    }

    @Override
    protected boolean codePointInRange(int codePoint) {
        return codePoint > 0 && codePoint < characters.length();
    }
}
