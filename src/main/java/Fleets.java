import java.util.Collection;
import java.util.HashSet;

public class Fleets extends HashSet<Fleet> {

    public Fleets() {
        super();
    }

    public Fleets(final Collection<? extends Fleet> c) {
        super(c);
    }

    public Fleets as(final Predicate<Fleet> predicate) {
        return Filter.filter(this, predicate);
    }

    public Fleet findFirstArriving(final Planet planet) {
        final Fleets enRoute = as(new Predicate<Fleet>() {
            public boolean apply(final Fleet obj) {
                return obj.getDestination().equals(planet);
            }
        });
        Fleet fleet = null;
        int turn = Integer.MAX_VALUE;
        for (final Fleet f : enRoute) {
            if (f.getTurnsRemaining() < turn) {
                fleet = f;
                turn = f.getTurnsRemaining();
            }
        }
        return fleet;
    }

    public Fleet findLastArriving(final Planet planet) {
        final Fleets enRoute = as(new Predicate<Fleet>() {
            public boolean apply(final Fleet obj) {
                return obj.getDestination().equals(planet);
            }
        });
        Fleet fleet = null;
        int turn = Integer.MIN_VALUE;
        for (final Fleet f : enRoute) {
            if (f.getTurnsRemaining() > turn) {
                fleet = f;
                turn = f.getTurnsRemaining();
            }
        }
        return fleet;
    }

    public int getShipCount() {
        int sum = 0;
        for (final Fleet f : this) {
            sum += f.getShipCount();
        }
        return sum;
    }

    public int getMyShipCount() {
        return as(new Predicate<Fleet>() {
            public boolean apply(final Fleet obj) {
                return Player.ME.equals(obj.getOwner());
            }
        }).getShipCount();
    }

    public int getEnemyShipCount() {
        return as(new Predicate<Fleet>() {
            public boolean apply(final Fleet obj) {
                return Player.NOT_ME.contains(obj.getOwner());
            }
        }).getShipCount();
    }

}
