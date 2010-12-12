import java.util.ArrayList;
import java.util.List;

public class DefaultUniverse extends AbstractUniverse {

    private static final Logger log = Logger.getLogger(DefaultUniverse.class);

    private static final Function<String,Integer> toInteger = new Function<String,Integer>() {
        @Override
        public Integer apply(final String input) {
            return Integer.parseInt(input);
        }
    };
    private static final Function<String,Double> toDouble = new Function<String,Double>() {
        @Override
        public Double apply(final String input) {
            return Double.parseDouble(input);
        }
    };

    private final Repository<Integer,Planet> planets;
    private final Repository<Integer,Fleet> fleets;
    private final List<FutureUniverse> states;
    private final ListenerSupport<TurnStartListener> turnStartListeners;
    private final ListenerSupport<TurnDoneListener> turnDoneListeners;
    private int planetId;
    private int turn;

    public DefaultUniverse() {
        super();
        this.planets = new Repository<Integer,Planet>(new Function<Planet,Integer>() {
            @Override
            public Integer apply(final Planet input) {
                return input.getId();
            }
        });
        this.fleets = new Repository<Integer,Fleet>(new Function<Fleet,Integer>() {
            @Override
            public Integer apply(final Fleet input) {
                return input.hashCode();
            }
        });
        this.states = new ArrayList<FutureUniverse>();
        this.turnStartListeners = new ListenerSupport<TurnStartListener>();
        this.turnDoneListeners = new ListenerSupport<TurnDoneListener>();
        this.planetId = 0;
    }

    public void add(final TurnStartListener aListener) {
        turnStartListeners.add(aListener);
    }

    public void add(final TurnDoneListener aListener) {
        turnDoneListeners.add(aListener);
    }

    @Override
    public Fleets getFleets() {
        return new Fleets(fleets.all());
    }

    @Override
    public Planets getPlanets() {
        return new Planets(planets.all());
    }

    @Override
    public int getTurn() {
        return turn;
    }

    @Override
    public FutureUniverse inFuture(final int turns) {
        return states.get(Math.max(0, Math.min(turns, states.size() - 1)));
    }


    public void remove(final TurnStartListener aListener) {
        turnStartListeners.remove(aListener);
    }

    public void remove(final TurnDoneListener aListener) {
        turnDoneListeners.remove(aListener);
    }

    public void turnStart(final int turn) {
        this.turn = turn;
        if (turn == 1) {
            log.info("Planet.getMaxDistance(): " + Planet.getMaxDistance());
            final List<FuturePlanet> futurePlanets = new ArrayList<FuturePlanet>(planets.size());
            for (final Planet planet : planets) {
                futurePlanets.add(planet.now());
            }
            final FutureUniverse state = new FutureUniverse(this, futurePlanets);
            add(TurnStartListener.class.cast(state));
            add(TurnDoneListener.class.cast(state));
            states.add(state);
            for (int turnDelta = 1; turnDelta <= Planet.getMaxDistance(); turnDelta++) {
                states.add(new FutureUniverse(states.get(turnDelta - 1)));
            }
            log.debug("Initialized future states");
            add(new TurnStartListener() {
                @Override
                public void turnStart(final int turn) {
                    for (final Planet planet : planets) {
                        log.info(planet.now().toString());
                    }
                }
            });
        }
        for (final TurnStartListener listener : turnStartListeners) {
            listener.turnStart(turn);
        }
    }

    public void turnDone() {
        for (final TurnDoneListener listener : turnDoneListeners) {
            listener.turnDone();
        }
        fleets.clear();
        planetId = 0;
    }


    public void update(String line) {
        final int commentBegin = line.indexOf('#');
        if (commentBegin >= 0) {
            line = line.substring(0, commentBegin);
        }
        line = line.trim();
        if (!line.isEmpty()) {
            String[] tokens = GameUpdateHandler.SPACE_RE.split(line);
            if (tokens.length > 0) {
                if ("P".equals(tokens[0]) && tokens.length == 6) {
                    if (!planets.has(planetId)) {
                        final Planet planet = new Planet(this, planetId, toInteger.apply(tokens[5]), toDouble.apply(tokens[1]), toDouble.apply(tokens[2]));
                        planets.add(planet);
                        DefaultUniverse.log.info("Added new planet: " + planet);
                    }
                    final Planet planet = planets.get(planetId);
                    planet.now().setOwner(Player.get(toInteger.apply(tokens[3])));
                    planet.now().setShipCount(toInteger.apply(tokens[4]));
                    planetId++;
                }
                else if ("F".equals(tokens[0]) && tokens.length == 7) {
                    //Mapper.map(Arrays.asList(tokens).subList(1, 7), toInteger);
                    addFleet(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
                }
            }
        }
    }

    private Fleet addFleet(final String owner, final String shipCount, final String source, final String destination, final String totalTurns, final String turnsRemaining) {
        final Fleet fleet = new Fleet(owner, shipCount, source, destination, totalTurns, turnsRemaining);
        fleets.add(fleet);
        //log.info("Added new fleet: " + fleet);
        log.info(fleet.toString());
        return fleet;
    }

}
