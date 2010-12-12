public class SelectedOrder {

    private static long nextId = 0;

    private final long id;
    private final Planet source;
    private final Planet destination;
    private final int shipCount;

    public SelectedOrder(final Planet source, final Planet destination, final int shipCount) {
        super();
        this.id = nextId++;
        this.source = source;
        this.destination = destination;
        this.shipCount = shipCount;
    }

    public Planet getSource() {
        return source;
    }

    public Planet getDestination() {
        return destination;
    }

    public int getShipCount() {
        return shipCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SelectedOrder that = (SelectedOrder) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "SelectedOrder(" + source + " -> " + destination + " #" + shipCount + ")";
    }
}
