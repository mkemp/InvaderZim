import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListenerSupport<T> implements Iterable<T> {

    private final List<T> theListeners;

    public ListenerSupport() {
        super();
        theListeners = new ArrayList<T>();
    }

    public void add(final T aListener) {
        theListeners.add(aListener);
    }

    public Iterator<T> iterator() {
        return theListeners.iterator();
    }

    public void remove(final T aListener) {
        theListeners.remove(aListener);
    }
}
