import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FutureUniverse implements TurnStartListener, TurnDoneListener {

    private static final Logger log = Logger.getLogger(FutureUniverse.class);

    private static final OwnerPredicate ENEMY = new OwnerPredicate(Player.ENEMY);
    private static final OwnerPredicate MINE = new OwnerPredicate(Player.ME);
    private static final Function<Planet,Integer> GET_GROWTH_RATE = new Function<Planet,Integer>() {
        @Override
        public Integer apply(final Planet input) {
            return input.getGrowthRate();
        }
    };
    private static final Function<FuturePlanet,Integer> GET_SHIP_COUNT = new Function<FuturePlanet,Integer>() {
        @Override
        public Integer apply(final FuturePlanet input) {
            return input.getShipCount();
        }
    };
    private static final Reduce<Integer> SUM = new Reduce<Integer>(Reduce.IntegerOperation.Add);

    private final Universe universe;
    private final FutureUniverse previous;
    private FutureUniverse next;
    private final Map<Planet,FuturePlanet> planets;
    private final int turnDelta;
    private int turn;
    private final Memoize<Planets> enemyPlanets;
    private final Memoize<Integer> enemyShipCount;
    private final Memoize<Integer> enemyPlanetShipCount;
    private final Memoize<Integer> enemyProduction;
    private final Memoize<Planets> myPlanets;
    private final Memoize<Integer> myShipCount;
    private final Memoize<Integer> myPlanetShipCount;
    private final Memoize<Integer> myProduction;
    private final Memoize<Fleets> fleets;
    private final Collection<Memoize<?>> caches;


    public FutureUniverse(final Universe universe, final Collection<FuturePlanet> futurePlanets) {
        this(universe, futurePlanets, null);
    }

    public FutureUniverse(final FutureUniverse previous) {
        this(
            previous.universe,
            new Mapper<FuturePlanet,FuturePlanet>(previous.planets.values(), new Function<FuturePlanet,FuturePlanet>() {
                @Override
                public FuturePlanet apply(final FuturePlanet input) {
                    return Planet.create(input);
                }
            }),
            previous
        );
    }

    private FutureUniverse(final Universe universe, final Iterable<FuturePlanet> futurePlanets, final FutureUniverse previous) {
        super();
        this.universe = universe;
        this.planets = new HashMap<Planet,FuturePlanet>();
        for (final FuturePlanet futurePlanet : futurePlanets) {
            this.planets.put(futurePlanet.getPlanet(), futurePlanet);
        }
        this.previous = previous;
        if (previous != null) {
            if (previous.next == null) {
                previous.next = this;
            }
            this.turnDelta = previous.turnDelta + 1;
            this.turn = previous.turn + 1;
        } else {
            this.turnDelta = 0;
            this.turn = 0;
        }
        final Function<Planet, Integer> getShipCount = Functions.compose(GET_SHIP_COUNT, Functions.forMap(planets));
        enemyPlanets = new Memoize<Planets>() {
            @Override
            protected Planets calculate() {
                final Planets enemyPlanets = new Planets();
                for (final FuturePlanet futurePlanet : new Filter<FuturePlanet>(planets.values(), ENEMY)) {
                    enemyPlanets.add(futurePlanet.getPlanet());
                }
                return enemyPlanets;
            }
        };
        enemyShipCount = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return getEnemyPlanetShipCount() + getFleets().getEnemyShipCount();
            }
        };
        enemyPlanetShipCount = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return SUM.reduce(new Mapper<Planet,Integer>(enemyPlanets.get(), getShipCount));
            }
        };
        enemyProduction = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return SUM.reduce(new Mapper<Planet,Integer>(enemyPlanets.get(), GET_GROWTH_RATE));
            }
        };
        myPlanets = new Memoize<Planets>() {
            @Override
            protected Planets calculate() {
                final Planets myPlanets = new Planets();
                for (final FuturePlanet futurePlanet : new Filter<FuturePlanet>(planets.values(), MINE)) {
                    myPlanets.add(futurePlanet.getPlanet());
                }
                return myPlanets;
            }
        };
        myShipCount = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return getMyPlanetShipCount() + getFleets().getMyShipCount();
            }
        };
        myPlanetShipCount = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return SUM.reduce(new Mapper<Planet,Integer>(myPlanets.get(), getShipCount));
            }
        };
        myProduction = new Memoize<Integer>() {
            @Override
            protected Integer calculate() {
                return SUM.reduce(new Mapper<Planet,Integer>(myPlanets.get(), GET_GROWTH_RATE));
            }
        };
        fleets = new Memoize<Fleets>() {
            @Override
            protected Fleets calculate() {
                return universe.findFleets(
                    new Predicate<Fleet>() {
                        public boolean apply(final Fleet obj) {
                            return obj.getTurnsRemaining() > turnDelta;
                        }
                    }
                );
            }
        };
        caches = Arrays.<Memoize<?>>asList(
            enemyPlanets, enemyShipCount, enemyPlanetShipCount, enemyProduction,
            myPlanets, myShipCount, myPlanetShipCount, myProduction,
            fleets
        );
    }

    public FuturePlanet get(final Planet planet) {
        return planets.get(planet);
    }

    public Planets getEnemyPlanets() {
        return enemyPlanets.get();
    }

    public int getEnemyShipCount() {
        return enemyShipCount.get();
    }

    public int getEnemyPlanetShipCount() {
        return enemyPlanetShipCount.get();
    }

    public int getEnemyProduction() {
        return enemyProduction.get();
    }

    public Planets getMyPlanets() {
        return myPlanets.get();
    }

    public int getMyShipCount() {
        return myShipCount.get();
    }

    public int getMyPlanetShipCount() {
        return myPlanetShipCount.get();
    }

    public int getMyProduction() {
        return myProduction.get();
    }

    public int getTurn() {
        return turn;
    }

    public int getTurnDelta() {
        return turnDelta;
    }

    @Override
    public void turnDone() {
        if (next != null) {
            next.turnDone();
        }
        for (final Memoize<?> cache : caches) {
            cache.reset();
        }
    }

    @Override
    public void turnStart(final int turn) {
        this.turn = turn + turnDelta;
        for (final FuturePlanet futurePlanet : planets.values()) {
            futurePlanet.setTurn(this.turn);
            futurePlanet.resolve(previous != null ? previous.planets.get(futurePlanet.getPlanet()) : null);
        }
        if (next != null) {
            next.turnStart(turn);
        }
        handleShipCount();
    }

    private Fleets getFleets() {
        return fleets.get();
    }

    private void handleShipCount() {
        final Planets myPlanets = getMyPlanets();
        final Planets enemyPlanets = getEnemyPlanets();
        final Planets myFrontLinePlanets = new Planets();
        if (!myPlanets.isEmpty()) {
            for (final Planet planet : enemyPlanets) {
                myFrontLinePlanets.add(myPlanets.findNearest(planet));
            }
        }
        for (final FuturePlanet futurePlanet : planets.values()) {
            if (Player.ME.equals(futurePlanet.getOwner())) {
                handleMine(futurePlanet, enemyPlanets, myFrontLinePlanets.contains(futurePlanet.getPlanet()));
            }
            else if (Player.NEUTRAL.equals(futurePlanet.getOwner())) {
                handleNeutral(futurePlanet);
            }
            else {
                handleEnemy(futurePlanet);
            }
        }
    }

    private void handleMine(final FuturePlanet futurePlanet, final Planets enemyPlanets, final boolean frontLine) {
        final Planet planet = futurePlanet.getPlanet();
        final Planet closestEnemy = enemyPlanets.findNearest(planet);
        final int distance = closestEnemy != null ? planet.distance(closestEnemy) : 1;
        //final int minimumShipCount = planet.getGrowthRate() * (frontLine ? (int) Math.ceil(Math.log(Planet.getMaxDistance()) / Math.log(distance)) : 1);
        final int shipCount = futurePlanet.getShipCount();
        if (next != null) {
            final FuturePlanet nextFuturePlanet = next.planets.get(planet);
            if (!Player.ME.equals(nextFuturePlanet.getOwner())) {
                // deficit
                futurePlanet.setRequiredShipCount(shipCount + nextFuturePlanet.getShipCount());
            } else {
                // surplus, but how much?
                final int surplusShipCount = Math.min(shipCount, nextFuturePlanet.getAvailableShipCount());
                int closestEnemyShipCount = 0;
                if (frontLine && closestEnemy != null && closestEnemy.getActualShipCount() > distance * planet.getGrowthRate()) {
                    closestEnemyShipCount = closestEnemy.getActualShipCount() - distance * planet.getGrowthRate();
                }
                futurePlanet.setRequiredShipCount(Math.max(shipCount - surplusShipCount, closestEnemyShipCount));
            }
        } else {
            // surplus, farthest known turn in the future
            int closestEnemyShipCount = 0;
            if (frontLine && closestEnemy != null && closestEnemy.getActualShipCount() > distance * planet.getGrowthRate()) {
                closestEnemyShipCount = closestEnemy.getActualShipCount() - distance * planet.getGrowthRate();
            }
            futurePlanet.setRequiredShipCount(Math.max(0, closestEnemyShipCount));
        }
    }

    private void handleEnemy(final FuturePlanet futurePlanet) {
        futurePlanet.setRequiredShipCount(0);
    }

    private void handleNeutral(final FuturePlanet futurePlanet) {
        futurePlanet.setRequiredShipCount(0);
        if (next != null) {
            final Planet planet = futurePlanet.getPlanet();
            final FuturePlanet nextFuturePlanet = next.planets.get(planet);
            if (Player.ME.equals(nextFuturePlanet.getOwner())) {
                futurePlanet.setRequiredShipCount(nextFuturePlanet.getRequiredShipCount() + futurePlanet.getShipCount());
            } else {
                futurePlanet.setRequiredShipCount(nextFuturePlanet.getRequiredShipCount());
            }
        }
    }

    private static class OwnerPredicate implements Predicate<FuturePlanet> {

        private final Player owner;

        private OwnerPredicate(final Player owner) {
            super();
            this.owner = owner;
        }

        @Override
        public boolean apply(final FuturePlanet obj) {
            return owner.equals(obj.getOwner());
        }
    }

    private static abstract class Memoize<T> {

        private boolean cached;
        private T value;

        public T get() {
            if (!cached) {
                value = calculate();
                cached = true;
            }
            return value;
        }

        protected abstract T calculate();

        public void reset() {
            cached = false;
            value = null;
        }
    }
}
