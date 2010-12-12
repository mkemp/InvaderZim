import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Context {

    private final Candidates candidates;
    private final Map<Planet,Candidates> byDestination;
    private final Map<Planet,Map<Integer,Candidates>> byDestinationAndTurnTotal;
    private final SortedSet<CompositeOrder> scoredOrders;
    private final Map<Pair<Planet>,Integer> selectedOrders;
    private final Orders orders;

    public Context() {
        super();
        candidates = new Candidates();
        byDestination = new HashMap<Planet,Candidates>();
        byDestinationAndTurnTotal = new HashMap<Planet,Map<Integer,Candidates>>();
        scoredOrders = new TreeSet<CompositeOrder>();
        selectedOrders = new HashMap<Pair<Planet>,Integer>();
        orders = new Orders();
    }

    public Candidates getCandidates() {
        return candidates;
    }

    public Map<Planet,Candidates> getByDestination() {
        return byDestination;
    }

    public Map<Planet,Map<Integer,Candidates>> getByDestinationAndTurnTotal() {
        return byDestinationAndTurnTotal;
    }

    public SortedSet<CompositeOrder> getScoredOrders() {
        return scoredOrders;
    }

    public Map<Pair<Planet>,Integer> getSelectedOrders() {
        return selectedOrders;
    }

    public Orders getOrders() {
        return orders;
    }
}
