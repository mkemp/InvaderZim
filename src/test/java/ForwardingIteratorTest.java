import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ForwardingIteratorTest {

    @Test
    public void test_next() {
        final Iterator<Integer> delegate = Arrays.asList(1, 2, 3).iterator();
        final Iterator<Integer> iterator = new ForwardingIterator<Integer>(delegate);
        assertEquals(new Integer(1), iterator.next());
        assertEquals(new Integer(2), iterator.next());
        assertEquals(new Integer(3), iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(delegate.hasNext());
    }

}
