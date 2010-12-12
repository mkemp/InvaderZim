import java.util.Collection;
import java.util.HashSet;

public class Candidates extends HashSet<Candidate> {

    public Candidates() {
        super();
    }

    public Candidates(final Collection<? extends Candidate> c) {
        super(c);
    }

    public Candidates as(final Predicate<Candidate> predicate) {
        return Filter.filter(this, predicate);
    }
}
