import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DefenseHeuristicStrategy extends BaseHeuristicStrategy {

    private static final Logger log = Logger.getLogger(DefenseHeuristicStrategy.class);

    @Override
    public boolean appliesToDestination(final Planet destination) {
        return destination.now().getAvailableShipCount() < 0;
    }

    @Override
    public Iterator<CompositeOrder> handleTurn(final Universe universe, final int turnTotal, final Planet destination, final Candidates candidates) {
        final PowerSet<Candidate> powerSet = new PowerSet<Candidate>(findNearestCandidates(destination, candidates));
        final Iterator<Set<Candidate>> itr = new Filter<Set<Candidate>>(powerSet, new Predicate<Set<Candidate>>() {
            public boolean apply(final Set<Candidate> obj) {
                return obj.size() > 1;
            }
        });
        return new DelegatingComputingIterator<Set<Candidate>,CompositeOrder>(itr) {
            @Override
            protected CompositeOrder compute() {
                while (itr.hasNext()) {
                    final Set<Candidate> candidates = itr.next();
                    int sourceShipCount = 0;
                    for (final Candidate candidate : candidates) {
                        sourceShipCount += candidate.getSource().inFuture(turnTotal - candidate.getDistance()).getShipCount();
                    }
                    final CompositeOrder order = order(universe, turnTotal, destination, Math.abs(destination.getAvailableShipCount()), candidates, sourceShipCount);
                    if (order != null) {
                        return order;
                    }
                }
                return null;
            }
        };
    }

    protected CompositeOrder order(final Universe universe, final int turnTotal, final Planet destination, final int destinationShipCount, final Set<Candidate> candidates, final int sourceShipCount) {
        if (destinationShipCount < sourceShipCount) {
            final Map<Planet,Integer> shipCountMap = calculateShipCountPerPlanet(destinationShipCount, candidates);
            final int score = score(universe, destination, destinationShipCount, turnTotal);
            final Set<OrderSegment> sources = Mapper.<Candidate,OrderSegment,Set<Candidate>,Set<OrderSegment>>map(candidates, new Function<Candidate,OrderSegment>() {
                @Override
                public OrderSegment apply(final Candidate candidate) {
                    final Planet source = candidate.getSource();
                    final int distance = candidate.getDistance();
                    final Integer shipCount = shipCountMap.containsKey(source) ? shipCountMap.get(source) : 0;
                    return new OrderSegment(source, shipCount, distance, turnTotal - distance);
                }
            });
            return new CompositeOrder(score, getStrategy(), destination, sources, turnTotal);
        }
        return null;
    }

    protected String getStrategy() {
        return "defense";
    }

    protected int scoreNeutral(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        double score = shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= destination.getGrowthRate();
        return (int) Math.ceil(score);
    }
}
