import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class BaseHeuristicStrategy extends BaseStrategy {

    private static final Logger log = Logger.getLogger(BaseHeuristicStrategy.class);

    protected final int SHIP_COUNT_WEIGHT = 60;
    protected final int TURN_COUNT_WEIGHT = 1200;

    public void takeAction(final Universe universe, final Context context) {
        final Set<CompositeOrder> scoredOrders = context.getScoredOrders();
        final int previouslyScored = scoredOrders.size();
        final Iterator<CompositeOrder> iterator = Chain.fromIterator(createOrderIterator(universe, context));
        while (iterator.hasNext()) {
            final CompositeOrder order = iterator.next();
            scoredOrders.add(order);
        }
        log.info("Scored " + (scoredOrders.size() - previouslyScored) + " candidate orders");
    }

    protected Iterator<Iterator<CompositeOrder>> createOrderIterator(final Universe universe, final Context context) {
        return new DelegatingComputingIterator<Map.Entry<Planet,Map<Integer,Candidates>>,Iterator<CompositeOrder>>(context.getByDestinationAndTurnTotal().entrySet()) {
            @Override
            protected Iterator<CompositeOrder> compute() {
                while(delegate().hasNext()) {
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

    public boolean appliesToDestination(final Planet destination) {
        return true;
    }

    public Iterator<CompositeOrder> handleDestination(final Universe universe, final Planet destination, final Map<Integer,Candidates> byTurnTotal) {
        return Chain.fromIterator(
            new DelegatingComputingIterator<Map.Entry<Integer,Candidates>,Iterator<CompositeOrder>>(byTurnTotal.entrySet()) {
                @Override
                protected Iterator<CompositeOrder> compute() {
                    while(delegate().hasNext()) {
                        final Map.Entry<Integer,Candidates> entry = delegate().next();
                        final int turnTotal = entry.getKey();
                        if (appliesToTurn(turnTotal)) {
                            return handleTurn(universe, turnTotal, destination, entry.getValue());
                        }
                    }
                    return null;
                }
            }
        );
    }

    public boolean appliesToTurn(final int turnTotal) {
        return true;
    }

    public Iterator<CompositeOrder> handleTurn(final Universe universe, final int turnTotal, final Planet destination, final Candidates candidates) {
        throw new UnsupportedOperationException();
    }

    public Map<Planet,Integer> calculateShipCountPerPlanet(final int destinationShipCount, final Set<Candidate> candidates) {
        return calculateShipCountPerPlanet(destinationShipCount, candidates, false);
    }

    public Map<Planet,Integer> calculateShipCountPerPlanet(int destinationShipCount, final Set<Candidate> candidates, final boolean prioritizeByProximity) {
        final Map<Planet,Integer> shipCountMap = new HashMap<Planet,Integer>();
        Comparator<Candidate> comparator = new Comparator<Candidate>() {
            public int compare(Candidate o1, Candidate o2) {
                return o1.getDistance() - o2.getDistance();
            }
        };
        if (!prioritizeByProximity) {
            comparator = Collections.reverseOrder(comparator);
        }
        final SortedSet<Candidate> sortedCandidates = new TreeSet<Candidate>(comparator);
        sortedCandidates.addAll(candidates);
        for (final Candidate candidate : sortedCandidates) {
            final int shipCount = Math.min(destinationShipCount + 1, candidate.getSource().getAvailableShipCount());
            if (shipCount > 0) {
                shipCountMap.put(candidate.getSource(), shipCount);
                destinationShipCount -= shipCount;
                if (destinationShipCount < 0) {
                    break;
                }
            }
        }
        return shipCountMap;
    }

    protected Candidates findNearestCandidates(final Planet destination, final Candidates candidates) {
        return findNearestCandidates(destination, candidates, 5);
    }

    protected Candidates findNearestCandidates(final Planet destination, final Candidates candidates, final Predicate<Planet> predicate) {
        return findNearestCandidates(destination, candidates, predicate, 5);
    }

    protected Candidates findNearestCandidates(final Planet destination, final Candidates candidates, final int number) {
        final Predicate<Planet> predicate = new Predicate<Planet>() {
            public boolean apply(final Planet obj) {
                return obj.getAvailableShipCount() > 0;
            }
        };
        return findNearestCandidates(destination, candidates, predicate, number);
    }

    protected Candidates findNearestCandidates(final Planet destination, final Candidates candidates, final Predicate<Planet> predicate, final int number) {
        final Map<Planet,Candidate> candidateMapping = new HashMap<Planet,Candidate>();
        for (final Candidate candidate : candidates) {
            candidateMapping.put(candidate.getSource(), candidate);
        }
        final Planets planets = findNearestPlanets(destination, new Planets(candidateMapping.keySet()), predicate, number);
        candidateMapping.keySet().retainAll(planets);
        return new Candidates(candidateMapping.values());
    }

    protected Planets findNearestPlanets(final Planet destination, final Planets planets) {
        return findNearestPlanets(destination, planets, 1);
    }

    protected Planets findNearestPlanets(final Planet destination, final Planets planets, final int number) {
        return findNearestPlanets(destination, planets, Predicates.<Planet>alwaysTrue(), number);
    }

    protected Planets findNearestPlanets(final Planet destination, final Planets planets, final Predicate<Planet> predicate, final int number) {
        final Planets allPlanets = new Planets(planets);
        final Planets somePlanets = new Planets();
        while (somePlanets.size() < number) {
            final Planet planet = allPlanets.findNearest(destination, predicate);
            if (planet != null) {
                somePlanets.add(planet);
                allPlanets.remove(planet);
            } else {
                break;
            }
        }
        return somePlanets;
    }

    protected int score(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        final int score;
        switch (destination.now().getOwner()) {
            case ME:
                score = scoreMe(universe, destination, shipCount, distance);
                break;
            case NEUTRAL:
                score = scoreNeutral(universe, destination, shipCount, distance);
                break;
            default:
                score = scoreEnemy(universe, destination, shipCount, distance);
                break;
        }
        return score;
    }

    protected int scoreEnemy(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        final FutureUniverse now = universe.now();
        final FutureUniverse future = universe.inFuture(distance);
        final Player owner = destination.inFuture(distance).getOwner();
        // start with ratio of my ships to enemy ships
        double multiplier = (double) now.getMyShipCount() / (double) now.getEnemyShipCount();
        // does my production get better?
        if (future.getMyProduction() + destination.getGrowthRate() > now.getMyProduction()) {
            multiplier *= (1.0 + (destination.getGrowthRate() / (10.0 * future.getMyProduction())));
        }
        // does enemy production get worse?
        if (Player.ENEMY.equals(owner) && future.getEnemyProduction() - destination.getGrowthRate() < now.getEnemyProduction()) {
            multiplier *= (1.0 + (destination.getGrowthRate() / (10.0 * future.getEnemyProduction())));
        }
        double score = 1.2 * shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= (destination.getGrowthRate() * Math.E * multiplier);
        return (int) Math.ceil(score);
    }

    protected int scoreMe(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        double score = shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= destination.getGrowthRate();
        return (int) Math.ceil(score);
    }

    protected int scoreNeutral(final Universe universe, final Planet destination, final int shipCount, final int distance) {
        final FutureUniverse now = universe.now();
        double multiplier = 1.0 - (double) destination.inFuture(distance).getShipCount() / (double) (now.getMyShipCount() + now.getEnemyShipCount());
        double score = shipCount * SHIP_COUNT_WEIGHT + distance * TURN_COUNT_WEIGHT;
        score /= (destination.getGrowthRate() * Math.max(multiplier, 0.01));
        return (int) Math.ceil(score);
    }
}
