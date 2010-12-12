import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class SupportHeuristicStrategy extends BaseHeuristicStrategy {

    private static final Logger log = Logger.getLogger(SupportHeuristicStrategy.class);

    @Override
    protected Iterator<Iterator<CompositeOrder>> createOrderIterator(final Universe universe, final Context context) {
        final Predicate<Candidate> predicate = new Predicate<Candidate>() {
            @Override
            public boolean apply(final Candidate obj) {
                return obj.getTurns().contains(0);
            }
        };
        final Planets enemyPlanets = universe.getEnemyPlanets();
        return new DelegatingComputingIterator<Candidate,Iterator<CompositeOrder>>(context.getCandidates().as(predicate)) {
            @Override
            protected Iterator<CompositeOrder> compute() {
                while(delegate().hasNext()) {
                    final Candidate candidate = delegate().next();
                    final Planet destination = candidate.getDestination();
                    if (appliesToDestination(destination)) {
                        CompositeOrder order = null;
                        if (destination.now().getAvailableShipCount() < 0) {
                            order = evaluateForDefense(universe, candidate);
                        } else if (!enemyPlanets.isEmpty()) {
                            order = evaluateForSupport(universe, candidate, enemyPlanets);
                        }
                        if (order != null) {
                            //log.critical(order.toString());
                            return Collections.singleton(order).iterator();
                        }
                    }
                }
                return null;
            }
        };
    }

    @Override
    public boolean appliesToDestination(final Planet destination) {
        return Player.ME.equals(destination.now().getOwner()) || destination.now().getAvailableShipCount() < 0;
    }

    private CompositeOrder evaluateForDefense(final Universe universe, final Candidate candidate) {
        final Planet source = candidate.getSource();
        final Planet destination = candidate.getDestination();
        final boolean greaterGrowthRate = destination.getGrowthRate() > source.getGrowthRate();
        final int shipCount = Math.min(
            greaterGrowthRate ? source.now().getShipCount() : source.now().getAvailableShipCount(),
            Math.abs(destination.now().getAvailableShipCount()) + 1
        );
        final int score = score(universe, candidate.getDestination(), shipCount, candidate.getDistance());
        final Set<OrderSegment> sources = Collections.singleton(new OrderSegment(source, shipCount, candidate.getDistance(), 0));
        return new CompositeOrder(score, "defense", destination, sources, candidate.getDistance());
    }

    private CompositeOrder evaluateForSupport(final Universe universe, final Candidate candidate, final Planets enemyPlanets) {
        final Planet source = candidate.getSource();
        final Planet destination = candidate.getDestination();
        final int sourceToEnemy = source.distance(enemyPlanets.findNearest(source));
        final int sourceToDestination = candidate.getDistance();
        final int destinationToEnemy = destination.distance(enemyPlanets.findNearest(destination));
        if (sourceToDestination + destinationToEnemy < sourceToEnemy * 1.2) {
            final int shipCount = Math.max(Math.min(source.getGrowthRate() * 2, source.now().getAvailableShipCount()), 0);
            final int score = score(universe, candidate.getDestination(), shipCount, candidate.getDistance());
            final Set<OrderSegment> sources = Collections.singleton(new OrderSegment(source, shipCount, candidate.getDistance(), 0));
            return new CompositeOrder(score, "supply", destination, sources, candidate.getDistance());
        }
        return null;
    }

    protected int scoreNeutral(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        double score = shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= destination.getGrowthRate();
        return (int) Math.ceil(score);
    }
}
