import java.util.Arrays;

public class OrderSegment {

    private final Planet source;
    private final int shipCount;
    private final int distance;
    private final int turnDelta;

    public OrderSegment(final Planet source, final int shipCount, final int distance, final int turnDelta) {
        super();
        this.source = source;
        this.shipCount = shipCount;
        this.distance = distance;
        this.turnDelta = turnDelta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderSegment that = (OrderSegment) o;
        return Arrays.asList(source, shipCount, turnDelta, distance).equals(
                Arrays.asList(that.source, that.shipCount, that.turnDelta, that.distance)
        );
    }

    public int getDistance() {
        return distance;
    }

    public int getShipCount() {
        return shipCount;
    }

    public Planet getSource() {
        return source;
    }

    public int getTurnDelta() {
        return turnDelta;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + shipCount;
        result = 31 * result + distance;
        result = 31 * result + turnDelta;
        return result;
    }

    @Override
    public String toString() {
        return "<S " + source + " #" + shipCount + " in (" + turnDelta + "+" + distance + ")>";
    }
}
