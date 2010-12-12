import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Orders implements Iterable<SelectedOrder> {

    private final boolean autoConsolidation;
    private final Map<Pair<Planet>,AtomicInteger> orderCandidates;

    public Orders() {
        this(true);
    }

    public Orders(final boolean autoConsolidation) {
        super();
        this.autoConsolidation = autoConsolidation;
        this.orderCandidates = new HashMap<Pair<Planet>,AtomicInteger>();
    }

    public void add(final Planet source, final Planet destination, final int shipCount) {
        final Pair<Planet> forward = new Pair<Planet>(source, destination);
        final Pair<Planet> reverse = forward.reverse();
        if (!orderCandidates.containsKey(forward)) {
            orderCandidates.put(forward, new AtomicInteger());
            orderCandidates.put(reverse, new AtomicInteger());
        }
        orderCandidates.get(forward).addAndGet(shipCount);
        if (autoConsolidation) {
            orderCandidates.get(reverse).addAndGet(-shipCount);
        }
    }

    public void clear() {
        orderCandidates.clear();
    }

    public Iterator<SelectedOrder> iterator() {
        return new DelegatingComputingIterator<Map.Entry<Pair<Planet>,AtomicInteger>,SelectedOrder>(orderCandidates.entrySet()) {

            protected SelectedOrder compute() {
                while(delegate().hasNext()) {
                    final Map.Entry<Pair<Planet>,AtomicInteger> value = delegate().next();
                    if (value.getValue().get() > 0) {
                        return new SelectedOrder(value.getKey().getFirst(), value.getKey().getLast(), value.getValue().get());
                    }
                }
                return null;
            }
        };
    }

    public List<SelectedOrder> toList() {
        final List<SelectedOrder> list = new ArrayList<SelectedOrder>(orderCandidates.size() / 2);
        for (final SelectedOrder order : this) {
            list.add(order);
        }
        return list;
    }

}
