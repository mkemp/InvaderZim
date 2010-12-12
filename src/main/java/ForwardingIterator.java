import java.util.Iterator;

public class ForwardingIterator<E> extends DelegatingComputingIterator<E,E> {

    public ForwardingIterator(final Iterable<E> iterable) {
        super(iterable);
    }

    public ForwardingIterator(final Iterator<E> iterator) {
        super(iterator);
    }

    @Override
    protected E compute() {
        if (delegate().hasNext()) {
            return delegate().next();
        }
        return null;
    }
}
