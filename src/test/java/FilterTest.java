import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FilterTest {

    private static final Predicate<Integer> predicate = new Predicate<Integer>() {
        @Override
        public boolean apply(final Integer obj) {
            return obj % 2 == 0;
        }
    };

    @Test
    public void iterator() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>emptySet(), predicate);
        assertSame(filter, filter.iterator());
    }

    @Test
    public void hasNext_iterableWithResults() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>singleton(2), predicate);
        assertTrue(filter.hasNext());
    }

    @Test
    public void hasNext_emptyIterable() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>emptySet(), predicate);
        assertFalse(filter.hasNext());
    }

    @Test
    public void hasNext_iterableWithNoResults() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>singleton(1), predicate);
        assertFalse(filter.hasNext());
    }

    @Test
    public void next_iterableWithResults() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>singleton(2), predicate);
        assertEquals(new Integer(2), filter.next());
        assertFalse(filter.hasNext());
    }

    @Test
    public void next_emptyIterable() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>emptySet(), predicate);
        try {
            filter.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    @Test
    public void next_iterableWithNoResults() {
        final Filter<Integer> filter = new Filter<Integer>(Collections.<Integer>singleton(1), predicate);
        try {
            filter.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    @Test
    public void filter_list() {
        final List<Integer> result = Filter.filter(Arrays.asList(1, 2, 3, 4, 5), predicate);
        assertEquals(Arrays.asList(2, 4), result);
    }

    @Test
    public void filter_set() {
        final Set<Integer> result = Filter.filter(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), predicate);
        assertEquals(new HashSet<Integer>(Arrays.asList(2, 4)), result);
    }

    @Test
    public void filter_sortedSet() {
        final SortedSet<Integer> result = Filter.filter(new TreeSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), predicate);
        assertEquals(new TreeSet<Integer>(Arrays.asList(2, 4)), result);
    }
}
