import org.junit.Test;

import static org.junit.Assert.fail;

public class UnmodifiableListIteratorTest {

    @Test
    public void add_throwsException() {
        final UnmodifiableListIterator<Object> itr = new MockUnmodifiableListIterator();
        try {
            itr.add(new Object());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    @Test
    public void set_throwsException() {
        final UnmodifiableListIterator<Object> itr = new MockUnmodifiableListIterator();
        try {
            itr.set(new Object());
            fail();
        } catch (UnsupportedOperationException e) {
            // no-op
        }
    }

    static class MockUnmodifiableListIterator extends UnmodifiableListIterator<Object> {

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public Object next() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public Object previous() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("test");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("test");
        }
    }
}
