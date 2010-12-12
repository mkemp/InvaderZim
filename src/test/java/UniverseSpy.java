import java.util.LinkedList;
import java.util.List;

public class UniverseSpy extends AbstractUniverse {

    private int turn;
    private List<String> lines;

    public UniverseSpy() {
        super();
        this.lines = new LinkedList<String>();
    }

    @Override
    public Fleets getFleets() {
        throw new UnsupportedOperationException();
    }

    public List<String> getLines() {
        return lines;
    }

    @Override
    public Planets getPlanets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTurn() {
        return turn;
    }

    @Override
    public FutureUniverse inFuture(final int turns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void turnDone() {
    }

    @Override
    public void turnStart(int turn) {
        this.turn = turn;
    }

    @Override
    public void update(final String line) {
        lines.add(line);
    }
}
