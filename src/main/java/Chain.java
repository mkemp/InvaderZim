import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Chain<E> extends AbstractComputingIterator<E> implements Iterable<E> {

    private Queue<Iterator<E>> iterators;
    private Iterator<E> iterator;

    public Chain(final Iterable<E>... iterables) {
        this(toIterators(iterables));
    }

    public Chain(final Iterator<E>... iterators) {
        this(new LinkedList<Iterator<E>>(Arrays.asList(iterators)));
    }

    private Chain(final Queue<Iterator<E>> iterators) {
        super(false);
        this.iterators = iterators;
        this.iterator = iterators.poll();
        initialize();
    }

    public static <E> Chain<E> fromIterable(final Iterable<Iterable<E>> iterables) {
        return new Chain<E>(toIterators(iterables));
    }

    public static <E> Chain<E> fromIterator(final Iterator<Iterator<E>> iterators) {
        return new Chain<E>(toIterators(iterators));
    }

    public boolean hasNext() {
        return super.hasNext() || iterator != null;
    }

    public Iterator<E> iterator() {
        return this;
    }

    protected E compute() {
        while (iterator != null && !iterator.hasNext()) {
            iterator = iterators.poll();
        }
        return (iterator != null) ? iterator.next() : null;
    }

    private static <E> Queue<Iterator<E>> toIterators(final Iterable<E>[] iterables) {
        return toIterators(Arrays.asList(iterables));
    }

    private static <E> Queue<Iterator<E>> toIterators(final Iterable<Iterable<E>> iterables) {
        final Queue<Iterator<E>> iterators = new LinkedList<Iterator<E>>();
        for (final Iterable<E> iterable : iterables) {
            iterators.add(iterable.iterator());
        }
        return iterators;
    }

    private static <E> Queue<Iterator<E>> toIterators(final Iterator<Iterator<E>> iterator) {
        final Queue<Iterator<E>> iterators = new LinkedList<Iterator<E>>();
        while(iterator.hasNext()) {
            iterators.add(iterator.next());
        }
        return iterators;
    }
}
