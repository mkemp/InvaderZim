import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReduceTest {

    @Test
    public void reduce_emptyIterable() {
        final Reduce<Object> reduce = new Reduce<Object>(new NullOperation());
        assertNull(reduce.reduce());
    }

    @Test
    public void reduce_nonEmptyIterable() {
        final Reduce<Object> reduce = new Reduce<Object>(new NullOperation());
        assertNull(reduce.reduce(null, null, null));
    }

    @Test
    public void reduce_withUseFirstValue() {
        final Reduce<String> reduce = new Reduce<String>(new LastSeenOperation<String>(), true);
        assertEquals("baz", reduce.reduce("foo", "bar", "baz"));
    }

    @Test
    public void reduce_emptyIterableWithUseFirstValue() {
        final Reduce<String> reduce = new Reduce<String>(new LastSeenOperation<String>(), true);
        assertNull(reduce.reduce());
    }

    @Test
    public void reduce_withoutUseFirstValue() {
        final Reduce<String> reduce = new Reduce<String>(new LastSeenOperation<String>(), false);
        assertEquals("baz", reduce.reduce("foo", "bar", "baz"));
    }

    @Test
    public void reduce_emptyIterableWithoutUseFirstValue() {
        final Reduce<String> reduce = new Reduce<String>(new LastSeenOperation<String>(), true);
        assertNull(reduce.reduce());
    }

    @Test
    public void booleanOperation_and() {
        assertTrue(Reduce.BooleanOperation.And.evaluate(true, true));
        assertFalse(Reduce.BooleanOperation.And.evaluate(true, false));
        assertFalse(Reduce.BooleanOperation.And.evaluate(false, true));
        assertFalse(Reduce.BooleanOperation.And.evaluate(false, false));
        assertTrue(Reduce.BooleanOperation.And.initialValue());
    }

    @Test
    public void booleanOperation_or() {
        assertTrue(Reduce.BooleanOperation.Or.evaluate(true, true));
        assertTrue(Reduce.BooleanOperation.Or.evaluate(true, false));
        assertTrue(Reduce.BooleanOperation.Or.evaluate(false, true));
        assertFalse(Reduce.BooleanOperation.Or.evaluate(false, false));
        assertFalse(Reduce.BooleanOperation.Or.initialValue());
    }

    @Test
    public void integerOperation_add() {
        assertEquals(new Integer(15), Reduce.IntegerOperation.Add.evaluate(10, 5));
        assertEquals(new Integer(0), Reduce.IntegerOperation.Add.initialValue());
    }

    @Test
    public void integerOperation_divide() {
        assertEquals(new Integer(2), Reduce.IntegerOperation.Divide.evaluate(10, 5));
        assertEquals(new Integer(1), Reduce.IntegerOperation.Divide.initialValue());
    }

    @Test
    public void integerOperation_max() {
        assertEquals(new Integer(10), Reduce.IntegerOperation.Max.evaluate(10, 5));
        assertEquals(new Integer(Integer.MIN_VALUE), Reduce.IntegerOperation.Max.initialValue());
    }

    @Test
    public void integerOperation_min() {
        assertEquals(new Integer(5), Reduce.IntegerOperation.Min.evaluate(10, 5));
        assertEquals(new Integer(Integer.MAX_VALUE), Reduce.IntegerOperation.Min.initialValue());
    }

    @Test
    public void integerOperation_multiply() {
        assertEquals(new Integer(50), Reduce.IntegerOperation.Multiply.evaluate(10, 5));
        assertEquals(new Integer(1), Reduce.IntegerOperation.Multiply.initialValue());
    }

    @Test
    public void integerOperation_subtract() {
        assertEquals(new Integer(5), Reduce.IntegerOperation.Subtract.evaluate(10, 5));
        assertEquals(new Integer(0), Reduce.IntegerOperation.Subtract.initialValue());
    }

    static class NullOperation implements Reduce.Operation<Object> {

        @Override
        public Object evaluate(final Object accumulator, final Object operand) {
            return null;
        }

        @Override
        public Object initialValue() {
            return null;
        }
    }

    static class LastSeenOperation<E> implements Reduce.Operation<E> {

        @Override
        public E evaluate(final E accumulator, final E operand) {
            return operand;
        }

        @Override
        public E initialValue() {
            return null;
        }
    }
}
