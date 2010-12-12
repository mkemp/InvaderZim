import java.io.InputStream;
import java.io.PrintStream;

public abstract class AbstractGame {

    private static final Logger log = Logger.getLogger(AbstractGame.class);

    private final Bot bot;
    protected final Universe universe;
    protected final InputStream in;
    protected final PrintStream out;

    public AbstractGame(final Bot bot, final Universe universe, final InputStream in, final PrintStream out) {
        super();
        this.bot = bot;
        this.universe = universe;
        this.in = in;
        this.out = out;
    }

    public void run() {
        final StringBuilder buffer = new StringBuilder();
        int c;
        try {
            while ((c = in.read()) >= 0) {
                switch (c) {
                    case '\n':
                        final String line = buffer.toString();
                        if ("go".equals(line)) {
                            final Context context = new Context();
                            turnStart(context);
                            bot.doTurn(universe, context);
                            turnDone(context);
                        } else {
                            universe.update(line);
                        }
                        buffer.delete(0, buffer.length());
                        break;
                    default:
                        buffer.append((char) c);
                        break;
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public abstract void turnDone(Context context);

    public abstract void turnStart(Context context);
}
