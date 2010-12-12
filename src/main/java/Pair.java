@SuppressWarnings("unchecked")
public class Pair<E> extends Tuple<E> {

    public Pair(final E one, final E two) {
        super(one, two);
    }

    public E getFirst() {
        return get(0);
    }

    public E getLast() {
        return get(1);
    }

    public Pair<E> reverse() {
        return new Pair<E>(getLast(), getFirst());
    }

}
