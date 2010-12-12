import java.util.NoSuchElementException;

public abstract class AbstractComputingIterator<E> extends UnmodifiableIterator<E> {

    private E next;

    public AbstractComputingIterator() {
        this(true);
    }

    public AbstractComputingIterator(final boolean initialize) {
        super();
        if (initialize) {
            initialize();
        }
    }

    protected abstract E compute();

    protected void initialize() {
        next = compute();
    }

    public boolean hasNext() {
        return next != null;
    }

    public E next() {
        if (hasNext()) {
            final E value = next;
            next = compute();
            return value;
        }
        throw new NoSuchElementException();
    }
}
