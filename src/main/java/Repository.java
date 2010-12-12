import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Repository<K,V> implements Iterable<V> {

    private final Function<V,K> toId;
    private final Map<K,V> map;

    public Repository(final Function<V,K> toId) {
        super();
        this.toId = toId;
        this.map = new HashMap<K,V>();
    }

    public void add(final V v) {
        map.put(toId.apply(v), v);
    }

    public Collection<V> all() {
        return map.values();
    }

    public void clear() {
        map.clear();
    }

    public V get(final K k) {
        return map.get(k);
    }

    public boolean has(final K k) {
        return map.containsKey(k);
    }

    @Override
    public Iterator<V> iterator() {
        return all().iterator();
    }

    public int size() {
        return map.size();
    }
}
