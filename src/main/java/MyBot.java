import java.util.Arrays;
import java.util.List;

public class MyBot implements Bot {

    private static final Logger log = Logger.getLogger(MyBot.class);
    static {
        //Logger.setLevel(Logger.Level.DEBUG);
        //Logger.setLevel(Logger.Level.ALL);
    }

    private List<Strategy> strategies;

    public MyBot() {
        super();
        strategies = Arrays.<Strategy>asList(
            new CandidateCreationStrategy(),
            new AttackHeuristicStrategy(),
            //new StreamingAttackHeuristicStrategy(),
            new KamikazeHeuristicStrategy(),
            new DefenseHeuristicStrategy(),
            new SupportHeuristicStrategy(),
            new OrderSelectionStrategy(),
            new OrderExecutionStrategy()
        );
    }

    public void doTurn(final Universe universe, final Context context) {
        try {
            log.debug(getClass().getSimpleName() + ".doTurn");
            final FutureUniverse state = universe.now();
            log.notice("My Production: " + state.getMyProduction() + " My Ship Count: " + state.getMyPlanetShipCount() + "/" + state.getMyShipCount());
            log.notice("Enemy Ship Count: " + state.getEnemyProduction() + " Enemy Ship Count: " + state.getEnemyPlanetShipCount() + "/" + state.getEnemyShipCount());
            for (final Strategy strategy : strategies) {
                log.debug(strategy.getClass().getSimpleName() + ".takeAction");
                 strategy.takeAction(universe, context);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static void main(final String[] args) {
        try {
            final Bot bot = new MyBot();
            final Game pw = new Game(bot);
            pw.run();
        } catch (Exception e) {
            log.error(e);
        }
    }
}

