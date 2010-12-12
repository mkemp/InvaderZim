import java.util.Iterator;

public abstract class DelegatingComputingIterator<F,T> extends AbstractComputingIterator<T> {

    private final Iterator<F> iterator;

    public DelegatingComputingIterator(final Iterable<F> iterable) {
        this(iterable.iterator());
    }

    public DelegatingComputingIterator(final Iterator<F> iterator) {
        super(false);
        this.iterator = iterator;
        initialize();
    }

    protected Iterator<F> delegate() {
        return iterator;
    }
}
