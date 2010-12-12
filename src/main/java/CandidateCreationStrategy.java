import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CandidateCreationStrategy extends BaseStrategy {

    private static final Logger log = Logger.getLogger(CandidateCreationStrategy.class);

    public void takeAction(final Universe universe, final Context context) {
        generateCandidatesFor(context);
        log.info("Created " + context.getCandidates().size() + " candidate source-destination pairs");
        final Map<Planet,Candidates> byDestination = context.getByDestination();
        final Map<Planet,Map<Integer,Candidates>> byDestinationAndTurnTotal = context.getByDestinationAndTurnTotal();
        for (final Candidate candidate : context.getCandidates()) {
            if (!byDestination.containsKey(candidate.getDestination())) {
                byDestination.put(candidate.getDestination(), new Candidates());
                byDestinationAndTurnTotal.put(candidate.getDestination(), new HashMap<Integer,Candidates>());
            }
            byDestination.get(candidate.getDestination()).add(candidate);
            for (final int turnDelta : candidate.getTurns()) {
                final int turnTotal = candidate.getDistance() + turnDelta;
                final Map<Integer,Candidates> byTurnTotal = byDestinationAndTurnTotal.get(candidate.getDestination());
                if (!byTurnTotal.containsKey(turnTotal)) {
                    byTurnTotal.put(turnTotal, new Candidates());
                }
                byTurnTotal.get(turnTotal).add(candidate);
            }
        }
        int sum = 0;
        for (final Map<Integer,Candidates> m : byDestinationAndTurnTotal.values()) {
            sum += m.size();
        }
        log.info("Created " + sum + " candidate destination turns");
    }

    public void generateCandidatesFor(final Context context) {
        final Candidates candidates = context.getCandidates();
        for (final Pair<Planet> pair : Planet.getPairs()) {
            final Planet source = pair.getFirst();
            final Planet destination = pair.getLast();
            final int distance = source.distance(destination);
            final Set<Integer> turns = new HashSet<Integer>();
            final int maxTurnDelta = Planet.getMaxDistance() - distance - 1;
            for (int turnDelta = 0; turnDelta < maxTurnDelta; turnDelta++) {
                if (Player.ME.equals(source.inFuture(turnDelta).getOwner())) {
                    turns.add(turnDelta);
                }
            }
            candidates.add(new Candidate(source, destination, distance, turns));
        }
    }
}
