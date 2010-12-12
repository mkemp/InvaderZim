import java.io.InputStream;
import java.io.PrintStream;

public class Game extends AbstractGame {

    private static final Logger log = Logger.getLogger(Game.class);

    private final Timer timer;
    private int turn;

    public Game(final Bot bot) {
        this(bot, System.in, System.out);
    }

    public Game(final Bot bot, final InputStream in, final PrintStream out) {
        this(bot, new DefaultUniverse(), in, out);
    }

    public Game(final Bot bot, final Universe universe, final InputStream in, final PrintStream out) {
        super(bot, universe, in, out);
        this.timer = new Timer();
        this.turn = 0;
    }

    public int getTurn() {
        return turn;
    }

    @Override
    public void turnDone(final Context context) {
        for (final SelectedOrder order : context.getOrders()) {
            // Sends an order to the game engine. An order is composed of a source
            // planet number, a destination planet number, and a number of ships. A
            // few things to keep in mind:
            //   * you can issue many orders per turn if you like.
            //   * the planets are numbered starting at zero, not one.
            //   * you must own the source planet. If you break this rule, the game
            //     engine kicks your bot out of the game instantly.
            //   * you can't move more ships than are currently on the source planet.
            //   * the ships will take a few turns to reach their destination. Travel
            //     is not instant. See the distance() function for more info.
            final int sourceId = order.getSource().getId();
            final int destinationId = order.getDestination().getId();
            final int shipCount = order.getShipCount();
            log.info(order.toString());
            out.println(sourceId + " " + destinationId + " " + shipCount);
        }

        out.println("go");
        out.flush();
        log.notice("EndTurn (" + timer.elapsed() + "ms total)");
        timer.reset();
        universe.turnDone();
    }

    @Override
    public void turnStart(final Context context) {
        log.notice("========================================");
        turn++;
        log.notice("StartTurn " + turn + " (" + timer.elapsed() + "ms setup)");
        universe.turnStart(turn);
    }

}
