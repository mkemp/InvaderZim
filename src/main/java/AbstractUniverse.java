import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUniverse implements Universe {

    @Override
    public Fleets findFleets(final Player owner) {
        return findFleets(owner, null, null, null);
    }

    @Override
    public Fleets findFleets(final Player owner, final Planet destination) {
        return findFleets(owner, null, destination, null);
    }

    @Override
    public Fleets findFleets(final Player owner, final Planet destination, final Predicate<Fleet> predicate) {
        return findFleets(owner, null, destination, predicate);
    }

    @Override
    public Fleets findFleets(final Player owner, final Predicate<Fleet> predicate) {
        return findFleets(owner, null, null, predicate);
    }

    @Override
    public Fleets findFleets(final Planet destination) {
        return findFleets(null, null, destination, null);
    }

    @Override
    public Fleets findFleets(final Planet destination, final Predicate<Fleet> predicate) {
        return findFleets(null, null, destination, predicate);
    }

    @Override
    public Fleets findFleets(final Predicate<Fleet> predicate) {
        return findFleets(null, null, null, predicate);
    }

    @Override
    public Fleets findFleets(final Player owner, final Planet source, final Planet destination, final Predicate<Fleet> predicate) {
        final Fleets result = getFleets();
        final List<Predicate<Fleet>> predicates = new ArrayList<Predicate<Fleet>>(4);
        if (owner != null) {
            predicates.add(new Predicate<Fleet>() {
                @Override
                public boolean apply(final Fleet obj) {
                    return owner.equals(obj.getOwner());
                }
            });
        }
        if (source != null) {
            predicates.add(new Predicate<Fleet>() {
                @Override
                public boolean apply(final Fleet obj) {
                    return source.equals(obj.getSource());
                }
            });
        }
        if (destination != null) {
            predicates.add(new Predicate<Fleet>() {
                @Override
                public boolean apply(final Fleet obj) {
                    return destination.equals(obj.getDestination());
                }
            });
        }
        if (predicate != null) {
            predicates.add(predicate);
        }
        return predicates.isEmpty() ? result : result.as(Predicates.<Fleet>and(predicates));
    }

    @Override
    public Planets findPlanets(final Player owner) {
        return findPlanets(owner, null, null);
    }

    @Override
    public Planets findPlanets(final Predicate<Planet> predicate) {
        return findPlanets(null, null, predicate);
    }

    @Override
    public Planets findPlanets(final Player owner, final Integer growthRate, final Predicate<Planet> predicate) {
        final Planets result = getPlanets();
        final List<Predicate<Planet>> predicates = new ArrayList<Predicate<Planet>>(3);
        if (owner != null) {
            predicates.add(new Predicate<Planet>() {
                @Override
                public boolean apply(final Planet obj) {
                    return owner.equals(obj.now().getOwner());
                }
            });
        }
        if (growthRate != null) {
            predicates.add(new Predicate<Planet>() {
                @Override
                public boolean apply(final Planet obj) {
                    return obj.getGrowthRate() == growthRate;
                }
            });
        }
        if (predicate != null) {
            predicates.add(predicate);
        }
        return predicates.isEmpty() ? result : result.as(Predicates.<Planet>and(predicates));
    }

    @Override
    public Fleets getMyFleets() {
        return findFleets(Player.ME);
    }

    @Override
    public Fleets getEnemyFleets() {
        return findFleets(Player.ENEMY);
    }

    @Override
    public Planets getMyPlanets() {
        return findPlanets(Player.ME);
    }

    @Override
    public Planets getEnemyPlanets() {
        return findPlanets(Player.ENEMY);
    }

    @Override
    public Planets getNeutralPlanets() {
        return findPlanets(Player.NEUTRAL);
    }

    @Override
    public Planets getNotMyPlanets() {
        final Planets planets = getPlanets();
        planets.removeAll(getMyPlanets());
        return planets;
    }

    @Override
    public FutureUniverse inFuture() {
        return inFuture(1);
    }

    @Override
    public FutureUniverse now() {
        return inFuture(0);
    }
}
