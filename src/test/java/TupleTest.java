import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TupleTest {

    @Test
    public void tuple_nullListTreatedAsEmpty() {
        final Tuple<Object> tuple = new Tuple<Object>((List<Object>) null);
        assertEquals(new Tuple<Object>(), tuple);
    }

    @Test
    public void get_negativeIndex() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.get(-1);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // no-op
        }
    }

    @Test
    public void get_validIndex() {
        final Tuple<String> tuple = new Tuple<String>("foo");
        assertEquals("foo", tuple.get(0));
    }

    @Test
    public void get_outOfRangeIndex() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.get(Integer.MAX_VALUE);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // no-op
        }
    }

    @Test
    public void size_empty() {
        final Tuple<Object> tuple = new Tuple<Object>();
        assertEquals(0, tuple.size());
    }

    @Test
    public void size_notEmpty() {
        final Tuple<Object> tuple = new Tuple<Object>(new Object());
        assertEquals(1, tuple.size());
    }

    @Test
    public void add_throwsException() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.add(new Object());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void clear_throwsException() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.clear();
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void remove_throwsException() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.remove(new Object());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void removeAll_throwsException() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.removeAll(Collections.<Object>emptySet());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void retainAll_throwsException() {
        final Tuple<Object> tuple = new Tuple<Object>();
        try {
            tuple.retainAll(Collections.<Object>emptySet());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }
}
