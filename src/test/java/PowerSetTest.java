import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PowerSetTest {

    @Test
    public void isEmptyAndSize_emptyIterable() {
        final Set<Set<Object>> powerSet = new PowerSet<Object>(Collections.<Object>emptySet());
        assertFalse(powerSet.isEmpty());
        assertEquals(1, powerSet.size());
    }

    @Test
    public void isEmptyAndSize_nonEmptyIterable() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar"));
        assertFalse(powerSet.isEmpty());
        assertEquals(4, powerSet.size());
    }

    @Test
    public void contains_emptySet() {
        final Set<Set<Object>> powerSet = new PowerSet<Object>(Collections.<Object>emptySet());
        assertTrue(powerSet.contains(Collections.<Object>emptySet()));
    }

    @Test
    public void contains_noElements() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar"));
        assertFalse(powerSet.contains(Collections.singleton("baz")));
    }

    @Test
    public void contains_partialElements() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar"));
        assertFalse(powerSet.contains(new HashSet<String>(Arrays.asList("bar", "baz"))));
    }

    @Test
    public void contains_oneElement() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar"));
        assertTrue(powerSet.contains(Collections.singleton("foo")));
    }

    @Test
    public void contains_allElements() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar"));
        assertTrue(powerSet.contains(new HashSet<String>(Arrays.asList("foo", "bar"))));
    }

    @Test
    public void iterator_emptyIterable() {
        final Set<Set<Object>> powerSet = new PowerSet<Object>(Collections.<Object>emptySet());
        final Iterator<Set<Object>> iterator = powerSet.iterator();
        assertEquals(Collections.<Object>emptySet(), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator_nonEmptyIterable() {
        final Set<Set<String>> powerSet = new PowerSet<String>(Arrays.asList("foo", "bar", "baz"));
        final Iterator<Set<String>> iterator = powerSet.iterator();
        assertEquals(Collections.<String>emptySet(), iterator.next());
        assertEquals(Collections.singleton("foo"), iterator.next());
        assertEquals(Collections.singleton("bar"), iterator.next());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "bar")), iterator.next());
        assertEquals(Collections.singleton("baz"), iterator.next());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "baz")), iterator.next());
        assertEquals(new HashSet<String>(Arrays.asList("bar", "baz")), iterator.next());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "bar", "baz")), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void add_throwsException() {
        final Set<Set<Object>> powerSet = new PowerSet<Object>(Collections.<Object>emptySet());
        try {
            powerSet.add(new HashSet<Object>());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void remove_throwsException() {
        final Set<Set<Object>> powerSet = new PowerSet<Object>(Collections.<Object>emptySet());
        try {
            powerSet.remove(new HashSet<Object>());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }
}
