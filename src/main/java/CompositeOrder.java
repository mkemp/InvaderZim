import java.util.Arrays;
import java.util.Set;

public class CompositeOrder implements Comparable<CompositeOrder> {

    private final int score;
    private final String strategy;
    private final Planet destination;
    private final Set<OrderSegment> sources;
    private final int turnTotal;

    public CompositeOrder(final int score, final String strategy, final Planet destination, final Set<OrderSegment> sources, final int turnTotal) {
        super();
        this.score = score;
        this.strategy = strategy;
        this.destination = destination;
        this.sources = sources;
        this.turnTotal = turnTotal;
    }

    public int compareTo(final CompositeOrder other) {
        if (score == other.score) {
            if (turnTotal == other.turnTotal) {
                final double x = Planet.getMaxX() - Planet.getMinX();
                final double y = Planet.getMaxY() - Planet.getMinY();
                final double xDelta = x - destination.getX();
                final double yDelta = y - destination.getY();
                final double otherXDelta = x - other.destination.getX();
                final double otherYDelta = y - other.destination.getY();
                return (int) Math.rint(Math.sqrt(xDelta * xDelta + yDelta * yDelta) - Math.sqrt(otherXDelta * otherXDelta + otherYDelta * otherYDelta));
            }
            return turnTotal - other.turnTotal;
        }
        return score - other.score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CompositeOrder that = (CompositeOrder) o;
        return Arrays.asList(destination, score, strategy, sources).equals(
                Arrays.asList(that.destination, that.score, that.strategy, that.sources)
        );
    }

    public Planet getDestination() {
        return destination;
    }

    public int getScore() {
        return score;
    }

    public Set<OrderSegment> getSources() {
        return sources;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getTurnTotal() {
        return turnTotal;
    }

    @Override
    public int hashCode() {
        int result = score;
        result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (sources != null ? sources.hashCode() : 0);
        result = 31 * result + turnTotal;
        return result;
    }

    @Override
    public String toString() {
        return "<O " + strategy + " " + score + " " + destination + " " + sources + ">";
    }
}
