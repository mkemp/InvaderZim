import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractComputingIteratorTest {

    @Test
    public void hasNext_emptyIterator() {
        final AbstractComputingIterator<Object> iterator = new EmptyComputingIterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void hasNext_iterableWithResults() {
        final AbstractComputingIterator<Integer> iterator = new ForwardingIterator<Integer>(Arrays.asList(1, 2, 3));
        assertTrue(iterator.hasNext());
    }

    @Test
    public void next_emptyIterator() {
        final AbstractComputingIterator<Object> iterator = new EmptyComputingIterator();
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    @Test
    public void next_iterableWithResults() {
        final AbstractComputingIterator<Integer> iterator = new ForwardingIterator<Integer>(Arrays.asList(1, 2, 3));
        assertEquals(new Integer(1), iterator.next());
        assertEquals(new Integer(2), iterator.next());
        assertEquals(new Integer(3), iterator.next());
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    static class EmptyComputingIterator extends AbstractComputingIterator<Object> {

        @Override
        protected Object compute() {
            return null;
        }
    }

}
