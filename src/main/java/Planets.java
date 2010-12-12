import java.util.Collection;
import java.util.HashSet;

public class Planets extends HashSet<Planet> {

    public Planets() {
        super();
    }

    public Planets(final Collection<? extends Planet> c) {
        super(c);
    }

    public Planets as(final Predicate<Planet> predicate) {
        return Filter.filter(this, predicate);
    }

    public double getAverageDistance(final Planet planet) {
        int totalDistance = 0;
        for (final Planet p : this) {
            totalDistance += planet.distance(p);
        }
        return (double) totalDistance / (double) size();
    }

    public int getMaxDistance(final Planet planet) {
        int distance = Integer.MIN_VALUE;
        for (final Planet p : this) {
            distance = Math.max(distance, planet.distance(p));
        }
        return distance;
    }

    public int getMinDistance(final Planet planet) {
        int distance = Integer.MAX_VALUE;
        for (final Planet p : this) {
            distance = Math.min(distance, planet.distance(p));
        }
        return distance;
    }

    public Planet findNearest(final Planet source) {
        return findNearest(source, Predicates.<Planet>alwaysTrue());
    }

    public Planet findNearest(final Planet source, final Predicate<Planet> predicate) {
        Planet destination = null;
        int distance = Integer.MAX_VALUE;
        for (final Planet planet : this) {
            if (predicate.apply(planet)) {
                int d = source.distance(planet);
                if (d < distance) {
                    distance = d;
                    destination = planet;
                }
            }
        }
        return destination;
    }

    public Planet findFarthest(final Planet source) {
        return findFarthest(source, Predicates.<Planet>alwaysTrue());
    }

    public Planet findFarthest(final Planet source, final Predicate<Planet> predicate) {
        Planet destination = null;
        int distance = Integer.MIN_VALUE;
        for (final Planet planet : this) {
            if (predicate.apply(planet)) {
                int d = source.distance(planet);
                if (d > distance) {
                    distance = d;
                    destination = planet;
                }
            }
        }
        return destination;
    }

    public Planet findWeakest() {
        return findWeakest(Predicates.<Planet>alwaysTrue());
    }

    public Planet findWeakest(final Predicate<Planet> predicate) {
        Planet destination = null;
        int ships = Integer.MAX_VALUE;
        for (final Planet planet : this) {
            if (predicate.apply(planet)) {
                if (planet.getAvailableShipCount() < ships) {
                    ships = planet.getAvailableShipCount();
                    destination = planet;
                }
            }
        }
        return destination;
    }

    public Planet findStrongest() {
        return findStrongest(Predicates.<Planet>alwaysTrue());
    }

    public Planet findStrongest(final Predicate<Planet> predicate) {
        Planet destination = null;
        int ships = Integer.MIN_VALUE;
        for (final Planet planet : this) {
            if (predicate.apply(planet)) {
                if (planet.getAvailableShipCount() > ships) {
                    ships = planet.getAvailableShipCount();
                    destination = planet;
                }
            }
        }
        return destination;
    }

}
