import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Planet implements Iterable<FuturePlanet> {

    private static final Map<Integer,Planet> REGISTRY = new TreeMap<Integer, Planet>();
    private static final Map<Pair<Planet>,Integer> DISTANCES = new HashMap<Pair<Planet>,Integer>();
    private static final Set<Pair<Planet>> PAIRS = new HashSet<Pair<Planet>>();

    private static double minX = Double.POSITIVE_INFINITY;
    private static double minY = Double.POSITIVE_INFINITY;
    private static double maxX = Double.NEGATIVE_INFINITY;
    private static double maxY = Double.NEGATIVE_INFINITY;
    private static int maxDistance = 1;
    private static int minGrowthRate = Integer.MAX_VALUE;
    private static int maxGrowthRate = Integer.MIN_VALUE;

    private final Universe universe;
    private final List<FuturePlanet> states;
    private final int id;
    private final int growthRate;
    private final double x;
    private final double y;

    public Planet(final Universe universe, final int planetId, final int growthRate, final double x, final double y) {
        super();
        this.universe = universe;
        this.states = new ArrayList<FuturePlanet>(20);
        this.states.add(new FuturePlanet(this));
        this.id = planetId;
        this.growthRate = growthRate;
        this.x = x;
        this.y = y;
        register(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Planet that = (Planet) o;
        return id == that.id;
    }

    public int getActualShipCount() {
        return now().getShipCount();
    }

    public int getAvailableShipCount() {
        return now().getAvailableShipCount();
    }

    public int getRequiredShipCount() {
        return now().getRequiredShipCount();
    }

    public int getId() {
        return id;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public Universe getUniverse() {
        return universe;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int distance(final Planet other) {
        return Planet.distance(this, other);
    }
    @Override
    public int hashCode() {
        return id;
    }

    public FuturePlanet inFuture() {
        return inFuture(1);
    }

    public FuturePlanet inFuture(final int turns) {
        return states.get(Math.max(0, Math.min(turns, states.size() - 1)));
    }

    public Iterator<FuturePlanet> iterator() {
        return states.iterator();
    }

    public FuturePlanet now() {
        return inFuture(0);
    }

    @Override
    public String toString() {
        return String.format("<P(%d) @(%.2f, %.2f) +%d>", id, x, y, growthRate);
    }

    public static FuturePlanet create(final FuturePlanet futurePlanet) {
        final Planet planet = futurePlanet.getPlanet();
        if (futurePlanet.getTurnDelta() + 1 < planet.states.size()) {
            return planet.states.get(futurePlanet.getTurnDelta());
        } else {
            final FuturePlanet next = new FuturePlanet(futurePlanet);
            planet.states.add(next);
            return next;
        }
    }

    public static int distance(final Planet one, final Planet two) {
        return DISTANCES.get(new Pair<Planet>(one, two));
    }

    public static Planet get(final int planetId) {
        return REGISTRY.get(planetId);
    }

    public static int getMinGrowthRate() {
        return minGrowthRate;
    }

    public static int getMaxGrowthRate() {
        return maxGrowthRate;
    }

    public static double getMinX() {
        return minX;
    }

    public static double getMinY() {
        return minY;
    }

    public static double getMaxX() {
        return maxX;
    }

    public static double getMaxY() {
        return maxY;
    }

    public static int getMaxDistance() {
        return maxDistance;
    }

    public static Collection<Pair<Planet>> getPairs() {
        return Collections.unmodifiableSet(PAIRS);
    }

    public static void reset() {
        REGISTRY.clear();
        PAIRS.clear();
        DISTANCES.clear();
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
        maxDistance = 1;
        minGrowthRate = Integer.MAX_VALUE;
        maxGrowthRate = Integer.MIN_VALUE;
    }

    public static List<Planet> toList() {
        return new ArrayList<Planet>(REGISTRY.values());
    }

    private static void register(final Planet planet) {
        REGISTRY.put(planet.id, planet);
        for (Planet other : REGISTRY.values()) {
            final double dx = planet.x - other.getX();
            final double dy = planet.y - other.getY();
            final int distance = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
            maxDistance = Math.max(maxDistance, distance);
            DISTANCES.put(new Pair<Planet>(planet, other), distance);
            DISTANCES.put(new Pair<Planet>(other, planet), distance);
            if (!planet.equals(other)) {
                PAIRS.add(new Pair<Planet>(planet, other));
                PAIRS.add(new Pair<Planet>(other, planet));
            }
        }
        minX = Math.min(minX, planet.x);
        minY = Math.min(minY, planet.y);
        maxX = Math.max(maxX, planet.x);
        maxY = Math.max(maxY, planet.y);
        minGrowthRate = Math.min(minGrowthRate, planet.growthRate);
        maxGrowthRate = Math.max(maxGrowthRate, planet.growthRate);
    }

}
