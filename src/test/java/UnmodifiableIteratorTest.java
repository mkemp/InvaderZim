import org.junit.Test;

import static org.junit.Assert.fail;

public class UnmodifiableIteratorTest {

    @Test
    public void remove_throwsException() {
        final UnmodifiableIterator<Object> itr = new MockUnmodifiableIterator();
        try {
            itr.remove();
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    static class MockUnmodifiableIterator extends UnmodifiableIterator<Object> {

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public Object next() {
            throw new UnsupportedOperationException("test");
        }
    }
}
