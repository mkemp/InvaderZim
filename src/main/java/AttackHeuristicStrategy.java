import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AttackHeuristicStrategy extends BaseHeuristicStrategy {

    private static final Logger log = Logger.getLogger(AttackHeuristicStrategy.class);

    private static final Reduce<Integer> SUM = new Reduce<Integer>(Reduce.IntegerOperation.Add);
    private static final Function<Candidate,Planet> CANDIDATE_TO_SOURCE = new Function<Candidate,Planet>() {
        @Override
        public Planet apply(final Candidate input) {
            return input.getSource();
        }
    };
    private static final Function<Planet,Integer> PLANET_TO_GROWTH_RATE = new Function<Planet,Integer>() {
        @Override
        public Integer apply(final Planet input) {
            return input.getGrowthRate();
        }
    };
    private static final Function<Candidate,Integer> CANDIDATE_TO_GROWTH_RATE = Functions.compose(PLANET_TO_GROWTH_RATE, CANDIDATE_TO_SOURCE);

    @Override
    protected Iterator<Iterator<CompositeOrder>> createOrderIterator(final Universe universe, final Context context) {
        final int shipCount = getMaxDestinationShipCount(universe);
        return new DelegatingComputingIterator<Map.Entry<Planet,Map<Integer,Candidates>>,Iterator<CompositeOrder>>(context.getByDestinationAndTurnTotal().entrySet()) {
            @Override
            protected Iterator<CompositeOrder> compute() {
                while(delegate().hasNext()) {
                    final Map.Entry<Planet,Map<Integer,Candidates>> entry = delegate().next();
                    final Planet destination = entry.getKey();
                    if (appliesToDestination(destination) && destination.now().getShipCount() < shipCount) {
                        return handleDestination(universe, destination, entry.getValue());
                    }
                }
                return null;
            }
        };
    }

    @Override
    public boolean appliesToDestination(final Planet destination) {
        return Player.NOT_ME.contains(destination.now().getOwner());
    }

    @Override
    public boolean appliesToTurn(final int turnTotal) {
        return turnTotal < Planet.getMaxDistance() / 2;
    }

    @Override
    public Iterator<CompositeOrder> handleTurn(final Universe universe, final int turnTotal, final Planet destination, final Candidates candidates) {
        final FuturePlanet state = destination.inFuture(turnTotal);
        final Function<Planet,Integer> planetToShipCount = new Function<Planet,Integer>() {
            @Override
            public Integer apply(final Planet planet) {
                final FuturePlanet state = planet.inFuture(turnTotal - planet.distance(destination));
                return state.getShipCount() - planet.getGrowthRate();
            }
        };
        final Function<Candidate,Integer> candidateToShipCount = Functions.compose(
            planetToShipCount,
            new Function<Candidate,Planet>() {
                @Override
                public Planet apply(final Candidate candidate) {
                    return candidate.getSource();
                }
            }
        );
        final Reduce<Integer> sum = new Reduce<Integer>(Reduce.IntegerOperation.Add);
        final PowerSet<Candidate> powerSet = new PowerSet<Candidate>(findNearestCandidates(destination, candidates));
        final Iterator<Set<Candidate>> itr = new Filter<Set<Candidate>>(powerSet, new Predicate<Set<Candidate>>() {
            public boolean apply(final Set<Candidate> obj) {
                return obj.size() > 0;
            }
        });
        return new DelegatingComputingIterator<Set<Candidate>,CompositeOrder>(itr) {
            @Override
            protected CompositeOrder compute() {
                while (itr.hasNext()) {
                    final Set<Candidate> candidates = itr.next();
                    int sourceShipCount = sum.reduce(new Mapper<Candidate,Integer>(candidates, candidateToShipCount));
                    CompositeOrder order = null;
                    if (Player.NEUTRAL.equals(destination.inFuture(turnTotal).getOwner()) || turnTotal <= 4) {
                        order = fullOrder(universe, turnTotal, destination, state.getShipCount(), candidates, sourceShipCount);
                    } else {
                        order = streamOrder(universe, turnTotal, destination, state.getShipCount(), candidates, sourceShipCount);
                    }
                    if (order != null) {
                        return order;
                    }
                }
                return null;
            }
        };
    }

    protected CompositeOrder fullOrder(final Universe universe, final int turnTotal, final Planet destination, final int destinationShipCount, final Set<Candidate> candidates, final int sourceShipCount) {
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

    protected CompositeOrder streamOrder(final Universe universe, final int turnTotal, final Planet destination, final int destinationShipCount, final Set<Candidate> candidates, final int sourceShipCount) {
        final Iterable<Integer> itr = new Mapper<Candidate,Integer>(candidates, CANDIDATE_TO_GROWTH_RATE);
        if (destinationShipCount + PLANET_TO_GROWTH_RATE.apply(destination) * turnTotal < sourceShipCount + SUM.reduce(itr) * turnTotal) {
            final Map<Planet,Integer> shipCountMap = new HashMap<Planet,Integer>();
            for (final Candidate candidate : candidates) {
                final Planet planet = candidate.getSource();
                final int shipCountDelta = planet.inFuture().getShipCount() - planet.now().getShipCount();
                shipCountMap.put(planet, Math.min(shipCountDelta * 3, planet.now().getAvailableShipCount()));
            }
            final int departingShipCount = SUM.reduce(shipCountMap.values());
            final int score = score(universe, destination, departingShipCount, turnTotal);
            final Set<OrderSegment> sources = Mapper.<Candidate,OrderSegment,Set<Candidate>,Set<OrderSegment>>map(candidates, new Function<Candidate,OrderSegment>() {
                @Override
                public OrderSegment apply(final Candidate candidate) {
                    final Planet source = candidate.getSource();
                    final int distance = candidate.getDistance();
                    final Integer shipCount = Math.max(shipCountMap.get(source), 0);
                    return new OrderSegment(source, shipCount, distance, turnTotal - distance);
                }
            });
            return new CompositeOrder(score, getStrategy(), destination, sources, turnTotal);
        }
        return null;
    }

    protected String getStrategy() {
        return "attack";
    }

    private int getMaxDestinationShipCount(final Universe universe) {
        int shipCount = Integer.MAX_VALUE;
        if (universe.getTurn() == 1) {
            final Planet myPlanet = universe.getMyPlanets().iterator().next();
            final Planet enemyPlanet = universe.getEnemyPlanets().iterator().next();
            final int distance = myPlanet.distance(enemyPlanet);
            shipCount = distance * myPlanet.getGrowthRate();
        }
        return shipCount;
    }
}
