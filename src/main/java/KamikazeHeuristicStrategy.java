import java.util.Iterator;
import java.util.Set;

public class KamikazeHeuristicStrategy extends AttackHeuristicStrategy {


    @Override
    public boolean appliesToDestination(final Planet destination) {
        return Player.ENEMY.equals(destination.now().getOwner());
    }

    @Override
    public boolean appliesToTurn(final int turnTotal) {
        return turnTotal < Math.min(Planet.getMaxDistance() / 5, 4);
    }

    @Override
    public Iterator<CompositeOrder> handleTurn(final Universe universe, final int turnTotal, final Planet destination, final Candidates candidates) {
        final Planets enemyPlanets = universe.getEnemyPlanets();
        final Predicate<Iterable<Candidate>> allOwned = Predicates.all(new Predicate<Candidate>() {
            @Override
            public boolean apply(Candidate obj) {
                return Player.ME.equals(obj.getSource().inFuture(turnTotal).getOwner());
            }
        });
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
        final Predicate<Planet> closeProximity = new Predicate<Planet>() {
            @Override
            public boolean apply(Planet obj) {
                return obj.distance(destination) < Math.min(Planet.getMaxDistance() / 5, 4) * 1.5;
            }
        };
        final PowerSet<Candidate> powerSet = new PowerSet<Candidate>(
            findNearestCandidates(destination, candidates.as(
                new Predicate<Candidate>() {
                    @Override
                    public boolean apply(Candidate obj) {
                        return obj.getSource().getActualShipCount() - obj.getSource().getGrowthRate() > 0;
                    }
                }
            ))
        );
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
                    if (allOwned.apply(candidates)) {
                        int sourceShipCount = sum.reduce(new Mapper<Candidate,Integer>(candidates, candidateToShipCount));
                        final Planets closestEnemyPlanets = findNearestPlanets(destination, enemyPlanets, closeProximity, enemyPlanets.size());
                        int destinationShipCount = sum.reduce(new Mapper<Planet,Integer>(closestEnemyPlanets, planetToShipCount));
                        final CompositeOrder compositeOrder = fullOrder(universe, turnTotal, destination, destinationShipCount, candidates, sourceShipCount);
                        if (compositeOrder != null) {
                            return compositeOrder;
                        }
                    }
                }
                return null;
            }
        };
    }

    @Override
    protected String getStrategy() {
        return "kamikaze";
    }
}
