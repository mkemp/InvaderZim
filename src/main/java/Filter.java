import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Filter<E> extends AbstractComputingIterator<E> implements Iterable<E> {

    private final Iterator<E> iterator;
    private final Predicate<E> predicate;

    public Filter(final Iterable<E> iterable, final Predicate<E> predicate) {
        this(iterable.iterator(), predicate);
    }

    public Filter(final Iterator<E> iterator, final Predicate<E> predicate) {
        super(false);
        this.iterator = iterator;
        this.predicate = predicate;
        initialize();
    }

    @SuppressWarnings("unchecked")
    public static <E,C extends List<E>> C filter(final C items, final Predicate<E> predicate) {
        return (C) filter(items, predicate, newContainer(items.getClass(), new ArrayList<E>()));
    }

    @SuppressWarnings("unchecked")
    public static <E,C extends Set<E>> C filter(final C items, final Predicate<E> predicate) {
        return (C) filter(items, predicate, newContainer(items.getClass(), new HashSet<E>()));
    }

    @SuppressWarnings("unchecked")
    public static <E,C extends SortedSet<E>> C filter(final C items, final Predicate<E> predicate) {
        return (C) filter(items, predicate, newContainer(items.getClass(), new TreeSet<E>()));
    }

    @Override
    protected E compute() {
        while(iterator.hasNext()) {
            final E obj = iterator.next();
            if (predicate.apply(obj)) {
                return obj;
            }
        }
        return null;
    }

    public boolean hasNext() {
        return super.hasNext() || iterator.hasNext();
    }

    public Iterator<E> iterator() {
        return this;
    }

    private static <E,C extends Collection<E>> C filter(final C items, final Predicate<E> predicate, final C filtered) {
        for (final E e : new Filter<E>(items, predicate)) {
            filtered.add(e);
        }
        return filtered;
    }

    private static <C> C newContainer(final Class<? extends C> klass, final C fallback) {
        try {
            return klass.newInstance();
        } catch (Exception e) {
            return fallback;
        }
    }
}
