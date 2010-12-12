import java.util.Map;

public class OrderExecutionStrategy extends BaseStrategy {

    private static final Logger log = Logger.getLogger(OrderExecutionStrategy.class);

    @Override
    public void takeAction(final Universe universe, final Context context) {
        final Orders orders = context.getOrders();
        final Map<Pair<Planet>,Integer> selectedOrders = context.getSelectedOrders();
        for (final Map.Entry<Pair<Planet>,Integer> entry : selectedOrders.entrySet()) {
            final Planet source = entry.getKey().getFirst();
            final Planet destination = entry.getKey().getLast();
            final int shipCount = entry.getValue();
            if (shipCount > 0) {
                orders.add(source, destination, shipCount);
            }
        }
    }
}
