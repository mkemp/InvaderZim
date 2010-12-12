import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PairTest {

    @Test
    public void testGetFirst() {
        final Pair<String> pair = new Pair<String>("foo", "bar");
        assertEquals("foo", pair.getFirst());
    }

    @Test
    public void testGetLast() {
        final Pair<String> pair = new Pair<String>("foo", "bar");
        assertEquals("bar", pair.getLast());
    }

    @Test
    public void testReverse() {
        final Pair<String> pair = new Pair<String>("foo", "bar");
        assertEquals(new Pair<String>("bar", "foo"), pair.reverse());
    }
}
