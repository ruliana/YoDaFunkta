package yodafunkta;

import static java.util.Arrays.asList;
import static yodafunkta.Functor.f;
import static yodafunkta.Functor.functor;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class FunctorTest extends TestCase {

    private static final Functor odd = functor("odd");
    private static boolean odd(int number) {
        return number % 2 == 1;
    }

    private static final Functor length = f("length");
    private static int length(String string) {
        return string == null ? 0 : string.length();
    }

    private static final Functor greaterThan = f("greaterThan");
    private static final Functor greaterThan4 = f("greaterThan", 4);
    private static boolean greaterThan(int length, String string) {
        return string != null && string.length() > length;
    }

    private static final Functor between = f("between");
    private static final Functor between2and = f("between", 2);
    private static boolean between(Integer min, int max, Integer target) {
        return min < target && target < max;
    }
    
    public void testRun() throws Exception {
        assertEquals(1, length.run("a"));
        assertEquals(4, length.run("test"));
    }
    
    public void testDeclareOnTheFlyAndRun() throws Exception {
        
        Functor myOwnLength = f("length");
        assertEquals(1, myOwnLength.run("a"));
        
        assertEquals(4, f("length").run("test"));
    }

    public void testEvaluate() throws Exception {
        assertFalse(greaterThan.evaluate(4, "Test"));
        assertTrue(greaterThan.evaluate(3, "Test"));
    }

    public void testMapping() throws Exception {
        List<String> list = asList("this", "is", "a", "test");
        
        assertEquals(asList(4, 2, 1, 4), length.map(list));
        assertEquals(asList(3, 7, 4), length.map("yet", "another", "test"));
    }

    public void testFilter() throws Exception {
        List<Integer> list = asList(1, 2, 3, 4, 5, 6);
        
        assertEquals(asList(1, 3, 5), odd.filter(list));
        assertEquals(asList(3, 5, 11, 13), odd.filter(2, 4, 3, 5, 6, 8, 11, 12, 13));
    }

    public void testCurrying() throws Exception {
        assertTrue(greaterThan4.evaluate("Testa"));
        assertFalse(greaterThan4.evaluate("Test"));

        Functor greaterThan3 = f("greaterThan", 3);
        assertTrue(greaterThan3.evaluate("Testa"));
        assertTrue(greaterThan3.evaluate("Test"));

        assertTrue(between.evaluate(2, 5, 3));
        assertFalse(between.evaluate(2, 5, 6));

        assertTrue(between2and.evaluate(5, 3));
        assertFalse(between2and.evaluate(5, 6));

        Functor between2and5 = between2and.param(5);
        assertTrue(between2and5.evaluate(3));
        assertFalse(between2and5.evaluate(6));
    }
    
    public void testCombine() throws Exception {
        assertTrue((Boolean) odd.of(length).run("another"));
        assertFalse(odd.of(length).<Boolean>run("test"));
    }
    
    public void testCurryingCombine() throws Exception {
        Functor between2and5OfLength = between.params(2, 5).of(length);
        assertEquals(asList("this", "yet", "test"), between2and5OfLength.filter("this", "is", "yet", "another", "test"));
    }
    
    public void testPerformance() throws Exception {
        
        // Creating the target
        List<String> target = new LinkedList<String>();
        for(int i = 0; i < 100000; i++) {
            target.add(String.valueOf(i));
        }
        
        // Hard code version
        long startHardCoded = System.currentTimeMillis();
        List<Integer> resultHardCoded = new LinkedList<Integer>();
        for (String string : target) {
            resultHardCoded.add(string == null ? 0 : string.length());
        }
        long elapsedHardCoded = System.currentTimeMillis() - startHardCoded;
        
        // Functor version
        long startFunctor = System.currentTimeMillis();
        List<Integer> resultFunctorVersion = length.map(target);
        long elapsedFunctor = System.currentTimeMillis() - startFunctor;
        
        assertEquals(resultHardCoded, resultFunctorVersion);
        assertTrue("Expected to be at most 6 times slower than the hard coded version", elapsedFunctor < 7* elapsedHardCoded);
        System.out.println(String.format("Hard coded: %3dms\n   Functor: %3dms", elapsedHardCoded, elapsedFunctor));
    }
}
