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
    public PropertyBasedTesting(Class<?> aClass) throws Throwable {
        super(aClass, Collections.emptyList());
        runners = Collections.singletonList(new ParametrizedRunner(aClass));
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }
}
