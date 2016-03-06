package pbt4j;

import org.junit.runner.Runner;
import org.junit.runners.Suite;

import java.util.*;


/**
 * @author Linas on 2016.03.05.
 */
public class PropertyBasedTesting extends Suite {

    private final List<Runner> runners;

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public PropertyBasedTesting(Class<?> klass) throws Throwable {
        super(klass, Collections.emptyList());
        runners = Collections.singletonList(new ParametrizedRunner(klass));
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }
}
