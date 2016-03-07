package pbt4j;

import pbt4j.annotations.*;
import pbt4j.generators.JsDataGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Linas on 2016.03.05.
 */
public class ParametrizedRunner extends BlockJUnit4ClassRunner {

    private final Class<?> klass;
    private final ScriptContext scriptContext = new SimpleScriptContext();

    public ParametrizedRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.klass = klass;
    }

    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
        //allow methods with args
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        Stream.of(klass.getAnnotationsByType(JsEval.class))
                .map(JsEval::value)
                .forEach(script -> JsDataGenerator.evalJs(script, scriptContext));
        return super.getChildren();
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Description description = describeMethod(method);
        if (isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            runLeaf(methodBlock(method), description, notifier);
        }
    }

    protected Description describeMethod(FrameworkMethod method) {
        return Optional.ofNullable(method.getAnnotation(Named.class))
                .map(named -> Description.createTestDescription(klass, named.value()))
                .orElse(describeChild(method));
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return (method.getMethod().getParameterCount() > 0 ?
                new ParametrizedStatement(method, test, scriptContext):
                super.methodInvoker(method, test));
    }

}
