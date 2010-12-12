import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChainTest {

    @Test
    public void iterator() {
        final Chain<Object> chain = new Chain<Object>(Collections.<Object>emptySet());
        assertSame(chain, chain.iterator());
    }

    @Test
    public void hasNext_iterableWithResults() {
        final Chain<String> chain = new Chain<String>(Collections.singleton("foo"));
        assertTrue(chain.hasNext());
    }

    @Test
    public void hasNext_emptyIterable() {
        final Chain<Object> chain = new Chain<Object>(Collections.<Object>emptySet());
        assertFalse(chain.hasNext());
    }

    @Test
    public void next_iterableWithResults() {
        final Chain<String> chain = new Chain<String>(Collections.singleton("foo"));
        assertEquals("foo", chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void next_emptyIterable() {
        final Chain<Object> chain = new Chain<Object>(Collections.<Object>emptySet());
        try {
            chain.next();
            fail();
        } catch (NoSuchElementException e) {
            // no-op
        }
    }

    @Test
    public void chain_multipleIterables() {
        final Chain<String> chain = new Chain<String>(
            Collections.singleton("foo"),
            Collections.singleton("bar"),
            Collections.singleton("baz")
        );
        assertEquals("foo", chain.next());
        assertEquals("bar", chain.next());
        assertEquals("baz", chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void chain_multipleIterators() {
        final Chain<String> chain = new Chain<String>(
            Collections.singleton("foo").iterator(),
            Collections.singleton("bar").iterator(),
            Collections.singleton("baz").iterator()
        );
        assertEquals("foo", chain.next());
        assertEquals("bar", chain.next());
        assertEquals("baz", chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void fromIterable_multipleIterables() {
        final Chain<String> chain = Chain.fromIterable(
            Arrays.<Iterable<String>>asList(
                Collections.singleton("foo"),
                Collections.singleton("bar"),
                Collections.singleton("baz")
            )
        );
        assertEquals("foo", chain.next());
        assertEquals("bar", chain.next());
        assertEquals("baz", chain.next());
        assertFalse(chain.hasNext());
    }

    @Test
    public void fromIterator_multipleIterators() {
        final Chain<String> chain = Chain.fromIterator(
            Arrays.asList(
                Collections.singleton("foo").iterator(),
                Collections.singleton("bar").iterator(),
                Collections.singleton("baz").iterator()
            ).iterator()
        );
        assertEquals("foo", chain.next());
        assertEquals("bar", chain.next());
        assertEquals("baz", chain.next());
        assertFalse(chain.hasNext());
    }

}
