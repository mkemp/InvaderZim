import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StreamingAttackHeuristicStrategy extends BaseHeuristicStrategy {

    private final List<Planet> rankedPlanets = new ArrayList<Planet>(40);

    @Override
    public void takeAction(final Universe universe, final Context context) {
        super.takeAction(universe, context);
    }

    protected Iterator<Iterator<CompositeOrder>> createOrderIterator(final Universe universe, final Context context) {
        final Map<Planet,Map<Integer,Candidates>> byDestinationAndTurnTotal = context.getByDestinationAndTurnTotal();
        final Iterator<Map.Entry<Planet,Map<Integer,Candidates>>> itr = new DelegatingComputingIterator<Planet,Map.Entry<Planet,Map<Integer,Candidates>>>(universe.getEnemyPlanets().iterator()) {
            @Override
            protected Map.Entry<Planet,Map<Integer,Candidates>> compute() {
                if (delegate().hasNext()) {
                    final Planet planet = delegate().next();
                    return new AbstractMap.SimpleImmutableEntry<Planet,Map<Integer,Candidates>>(planet, byDestinationAndTurnTotal.get(planet));
                }
                return null;
            }
        };
        return new DelegatingComputingIterator<Map.Entry<Planet,Map<Integer,Candidates>>,Iterator<CompositeOrder>>(itr) {
            @Override
            protected Iterator<CompositeOrder> compute() {
                while (delegate().hasNext()) {
                    final Map.Entry<Planet,Map<Integer,Candidates>> entry = delegate().next();
                    final Planet destination = entry.getKey();
                    if (appliesToDestination(destination)) {
                        return handleDestination(universe, destination, entry.getValue());
                    }
                }
                return null;
            }
        };
    }

    @Override
    public Iterator<CompositeOrder> handleTurn(final Universe universe, final int turnTotal, final Planet destination, final Candidates candidates) {
        final Iterator<Candidate> itr = new Filter<Candidate>(candidates, new Predicate<Candidate>() {
            public boolean apply(final Candidate obj) {
                return obj.getTurns().contains(0);
            }
        });
        return new DelegatingComputingIterator<Candidate,CompositeOrder>(itr) {
            @Override
            protected CompositeOrder compute() {
                while (itr.hasNext()) {
                    final Candidate candidate = itr.next();
                    final Planet source = candidate.getSource();
                    int shipCount = Math.min(source.now().getAvailableShipCount(), source.getGrowthRate() * 3);
                    final CompositeOrder order = streamOrder(universe, turnTotal, destination, candidate, shipCount);
                    if (order != null) {
                        return order;
                    }
                }
                return null;
            }
        };
    }

    protected CompositeOrder streamOrder(final Universe universe, final int turnTotal, final Planet destination, final Candidate candidate, final int shipCount) {
        final int score = score(universe, destination, shipCount, turnTotal);
        final Planet source = candidate.getSource();
        final int distance = candidate.getDistance();
        final Set<OrderSegment> sources = Collections.singleton(new OrderSegment(source, shipCount, distance, turnTotal - distance));
        return new CompositeOrder(score, getStrategy(), destination, sources, turnTotal);
    }

    protected String getStrategy() {
        return "stream";
    }

    protected int scoreEnemy(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        double score = 1.2 * shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= destination.getGrowthRate();
        return (int) Math.ceil(score);
    }
}
