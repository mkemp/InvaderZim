import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderSelectionStrategy extends BaseStrategy {

    private static final Logger log = Logger.getLogger(OrderSelectionStrategy.class);

    @Override
    public void takeAction(final Universe universe, final Context context) {
        final AvailableShipCounts availableShipCounts = new AvailableShipCounts(universe);
        final SortedSet<CompositeOrder> scoredOrders = context.getScoredOrders();
        if (!scoredOrders.isEmpty()) {
            final int cutoff = (int) Math.rint((scoredOrders.first().getScore() * 0.75) + (scoredOrders.last().getScore() * 0.75));
            //log.critical("cutoff = " + cutoff);
            final Map<Pair<Planet>,AtomicInteger> orders = new HashMap<Pair<Planet>,AtomicInteger>();
            for (final CompositeOrder order : scoredOrders) {
                final boolean myPlanet = Player.ME.equals(order.getDestination().now().getOwner());
                final boolean executable = availableShipCounts.executable(order);
                final int remainingShipCount = availableShipCounts.get(order.getDestination(), order.getTurnTotal());
                if (executable && (remainingShipCount <= 0 || myPlanet) && order.getScore() <= cutoff) {
                    for (final Map.Entry<Pair<Planet>,Integer> entry : availableShipCounts.execute(order).entrySet()) {
                        if (!orders.containsKey(entry.getKey())) {
                            orders.put(entry.getKey(), new AtomicInteger());
                        }
                        orders.get(entry.getKey()).addAndGet(entry.getValue());
                    }
                }
            }
            final Map<Pair<Planet>,Integer> selectedOrders = context.getSelectedOrders();
            for (final Map.Entry<Pair<Planet>,AtomicInteger> entry : orders.entrySet()) {
                selectedOrders.put(entry.getKey(), entry.getValue().get());
            }
        }
    }

    private static class AvailableShipCounts {

        private final Map<Planet,List<AtomicInteger>> availableShipCounts;

        public AvailableShipCounts(final Universe universe) {
            super();
            availableShipCounts = new HashMap<Planet,List<AtomicInteger>>();
            final Planets planets = universe.getPlanets();
            for (final Planet planet : planets) {
                availableShipCounts.put(planet, new ArrayList<AtomicInteger>(Planet.getMaxDistance() + 1));
            }
            for (int turnDelta = 0; turnDelta <= Planet.getMaxDistance(); turnDelta++) {
                final FutureUniverse futureUniverse = universe.inFuture(turnDelta);
                for (final Planet planet : planets) {
                    final FuturePlanet futurePlanet = futureUniverse.get(planet);
                    if (Player.ME.equals(futurePlanet.getOwner())) {
                        availableShipCounts.get(planet).add(new AtomicInteger(futurePlanet.getAvailableShipCount()));
                    } else {
                        availableShipCounts.get(planet).add(new AtomicInteger(-Math.abs(futurePlanet.getAvailableShipCount())));
                    }
                }
            }
        }

        public boolean executable(final CompositeOrder order) {
            for (final OrderSegment source : order.getSources()) {
                final int shipCount = get(source.getSource(), source.getTurnDelta());
                if (shipCount < source.getShipCount()) {
                    return false;
                }
            }
            return true;
        }

        public Map<Pair<Planet>,Integer> execute(final CompositeOrder order) {
            final Map<Pair<Planet>,Integer> orders = new HashMap<Pair<Planet>,Integer>();
            final List<AtomicInteger> destinationShipCounts = availableShipCounts.get(order.getDestination()).subList(order.getTurnTotal(), Planet.getMaxDistance() + 1);
            final boolean myPlanet = Player.ME.equals(order.getDestination().inFuture(order.getTurnTotal()).getOwner());
            for (final OrderSegment source : order.getSources()) {
                final List<AtomicInteger> shipCounts = availableShipCounts.get(source.getSource());
                if (shipCounts.get(source.getTurnDelta()).get() > 0 && source.getShipCount() > 0) {
                    log.info(order.getStrategy() + " " + order.getScore() + " #" + source.getShipCount() + " " + source.getSource() + " -> " + order.getDestination() + " in (" + source.getTurnDelta() + "+" + source.getDistance() + ")");
                    if (source.getTurnDelta() == 0) {
                        final Pair<Planet> pair = new Pair<Planet>(source.getSource(),order.getDestination());
                        orders.put(pair, source.getShipCount());
                    }
                    for (final AtomicInteger shipCount : shipCounts.subList(source.getTurnDelta(), shipCounts.size())) {
                        shipCount.addAndGet(-source.getShipCount());
                    }
                    if (!myPlanet) {
                        for (final AtomicInteger shipCount : destinationShipCounts) {
                            shipCount.addAndGet(source.getShipCount());
                        }
                    }
                }
            }
            return orders;
        }

        public int get(final Planet planet, final int turnDelta) {
            return availableShipCounts.get(planet).get(turnDelta).get();
        }
    }
}
