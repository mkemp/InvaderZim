import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Mapper<F,T> extends AbstractComputingIterator<T> implements Iterable<T>{

    private final Iterator<F> iterator;
    private final Function<F,T> function;

    public Mapper(final Iterable<F> iterable, final Function<F,T> function) {
        this(iterable.iterator(), function);
    }

    public Mapper(final Iterator<F> iterator, final Function<F,T> function) {
        super(false);
        this.iterator = iterator;
        this.function = function;
        initialize();
    }

    @SuppressWarnings("unchecked")
    public static <F,T,CF extends List<F>,CT extends List<T>> CT map(final CF items, final Function<F,T> function) {
        return (CT) map(items, function, newContainer(items.getClass(), new ArrayList<T>()));
    }

    @SuppressWarnings("unchecked")
    public static <F,T,CF extends Set<F>,CT extends Set<T>> CT map(final CF items, final Function<F,T> function) {
        return (CT) map(items, function, newContainer(items.getClass(), new HashSet<T>()));
    }

    @SuppressWarnings("unchecked")
    public static <F,T,CF extends SortedSet<F>,CT extends SortedSet<T>> CT map(final CF items, final Function<F,T> function) {
        return (CT) map(items, function, newContainer(items.getClass(), new TreeSet<T>()));
    }

    @Override
    protected T compute() {
        if (iterator.hasNext()) {
            return function.apply(iterator.next());
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    private static <F,T,CF extends Collection<F>, CT extends Collection<T>> CT map(CF items, Function<F,T> function, CT mapped) {
        for (final T t : new Mapper<F,T>(items, function)) {
            mapped.add(t);
        }
        return mapped;
    }

    private static <C> C newContainer(final Class<? extends C> klass, final C fallback) {
        try {
            return klass.newInstance();
        } catch (Exception e) {
            return fallback;
        }
    }
}
