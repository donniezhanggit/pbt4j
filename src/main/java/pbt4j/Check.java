package pbt4j;

import org.junit.Assert;

/**
 * @author Linas on 2016.03.07.
 */
public interface Check {

    static void assertException(String message, Class<? extends Throwable> expectedException, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail(message);
        } catch (Throwable e) {
            Assert.assertEquals(message, expectedException, e.getClass());
        }
    }

    static void assertException(Class<? extends Throwable> expectedException, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("Expected exception " + expectedException + " was not thrown.");
        } catch (Throwable e) {
            Assert.assertEquals("Expected " + expectedException + " but " + e + " was thrown.", expectedException, e.getClass());
        }
    }

    static <E extends Throwable> void assertException(String message, E expectedException, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail(message);
        } catch (Throwable e) {
            Assert.assertEquals(message, expectedException, e);
        }
    }

    static <E extends Throwable> void assertException(E expectedException, Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("Expected exception " + expectedException + " was not thrown.");
        } catch (Throwable e) {
            Assert.assertEquals("Expected " + expectedException + " but " + e + " was thrown.", expectedException, e);
        }
    }

}
