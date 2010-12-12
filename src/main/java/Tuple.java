import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tuple<E> extends AbstractCollection<E> {

    private final List<E> items;

    public Tuple(E... items) {
        this(Arrays.asList(items));
    }

    public Tuple(final List<E> items) {
        super();
        this.items = items != null ? items : Collections.<E>emptyList();
    }

    public boolean add(final E e) {
        throw new UnsupportedOperationException("Tuples are immutable");
    }

    public void clear() {
        throw new UnsupportedOperationException("Tuples are immutable");
    }

    public Tuple<E> concat(final Tuple<E> other) {
        final List<E> copy = new ArrayList<E>(size() + other.size());
        copy.addAll(items);
        copy.addAll(other.items);
        return new Tuple<E>(copy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Tuple<E> tuple = (Tuple<E>) o;

        return items.equals(tuple.items);
    }

    public E get(final int index) {
        return items.get(index);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    public Iterator<E> iterator() {
        return items.iterator();
    }

    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Tuples are immutable");
    }

    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Tuples are immutable");
    }

    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Tuples are immutable");
    }

    public int size() {
        return items.size();
    }

    public Tuple<E> subTuple(final int fromIndex) {
        return subTuple(fromIndex, size());
    }

    public Tuple<E> subTuple(final int fromIndex, final int toIndex) {
        return new Tuple<E>(items.subList(fromIndex, toIndex));
    }

}
