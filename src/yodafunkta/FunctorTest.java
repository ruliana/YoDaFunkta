package yodafunkta;

import static java.util.Arrays.asList;
import static yodafunkta.Functor.f;
import static yodafunkta.Functor.functor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    
    private static final Functor sum = f("sum");
    private static int sum(int a, int b) {
        return a + b;
    }

    private static final Functor join = f("join");
    private static String join(String a, String b) {
        return a + ", " + b;
    }
    private static String join(int a, int b) {
        return String.valueOf(a) + String.valueOf(b);
    }
    
    private static final Functor intGenerator = f("intGenerator");
    private static int intGenerator(int i) {
        return i + 1;
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
    
    public void testFoldLeft() throws Exception {
        
        assertEquals(15, (int) sum.foldLeft(0, asList(1, 2, 3, 4, 5)));
        assertEquals(15, (int) (Integer) sum.foldLeft(asList(1, 2, 3, 4, 5)));
        assertEquals("this, is, a, test", join.fold("this", "is", "a", "test"));
    }
    
    public void testFunctorPolimorphism() throws Exception {
        
        assertEquals("a, b", join.run("a", "b"));
        assertEquals("12", join.run(1, 2));
    }
    
    public void testInfiniteList() throws Exception {
        
        InfiniteList<Integer> infiniteList = intGenerator.infiniteList(0);
        assertEquals(new Integer(0), infiniteList.peek());
        assertEquals(new Integer(0), infiniteList.poll());
        assertEquals(new Integer(1), infiniteList.poll());
        assertEquals(asList(0), infiniteList.subList(0, 1));
        assertEquals(asList(1, 2, 3), infiniteList.subList(1, 4));
        assertEquals(asList(5, 6), infiniteList.subList(5, 7));
        
        Iterator<Integer> iterator = infiniteList.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new Integer(0), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new Integer(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new Integer(2), iterator.next());
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
        System.out.println(String.format("Hard coded: %3dms\n   Functor: %3dms\nSlowliness: %3.2fx", elapsedHardCoded, elapsedFunctor, (double) elapsedFunctor / elapsedHardCoded));
        assertTrue("Expected to be at most 9 times slower than the hard coded version", elapsedFunctor < 9 * elapsedHardCoded);
    }
}
