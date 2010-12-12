import java.util.Collections;
import java.util.Set;

public class Candidate {

    private Planet source;
    private Planet destination;
    private int distance;
    private Set<Integer> turns;

    public Candidate(final Planet source, final Planet destination, final int distance, final Set<Integer> turns) {
        super();
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.turns = Collections.unmodifiableSet(turns);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Candidate candidate = (Candidate) o;

        if (destination != null ? !destination.equals(candidate.destination) : candidate.destination != null)
            return false;
        if (source != null ? !source.equals(candidate.source) : candidate.source != null) return false;
        if (turns != null ? !turns.equals(candidate.turns) : candidate.turns != null) return false;

        return true;
    }

    public Planet getSource() {
        return source;
    }

    public Planet getDestination() {
        return destination;
    }

    public int getDistance() {
        return distance;
    }

    public Set<Integer> getTurns() {
        return turns;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (turns != null ? turns.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "<C(" + distance + ") " + source + " -> " + destination + " on " + turns + ">";
    }
}
