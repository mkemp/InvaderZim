import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {

    public enum Level {
        ALL,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        NOTICE,
        CRITICAL,
        OFF
    }

    private static Level level = Level.OFF;
    private static PrintWriter writer;

    public static Level getLevel() {
        return Logger.level;
    }

    public static void setLevel(final Level level) {
        Logger.level = level;
    }

    public static Logger getLogger(final Class klass) {
        return getLogger(klass.getSimpleName());
    }

    public static Logger getLogger(final String name) {
        return new Logger(name);
    }

    private static void ensureWriterConfigured() throws IOException {
        if (Logger.writer == null && Logger.level != Level.OFF) {
            final String path = System.getProperty("logger.directory", ".");
            final String file = System.getProperty("logger.file", "MyBot.log");
            Logger.writer = new PrintWriter(new BufferedWriter(new FileWriter(path + "/" + file)));
        }
    }

    private final String name;

    private Logger(final String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void debug(final String message) {
        log(Level.DEBUG, message);
    }

    public void info(final String message) {
        log(Level.INFO, message);
    }

    public void warn(final String message) {
        log(Level.WARN, message);
    }

    public void error(final String message) {
        log(Level.ERROR, message);
    }

    public void error(final Throwable throwable) {
        log(Level.ERROR, throwable.toString(), throwable);
    }

    public void notice(final String message) {
        log(Level.NOTICE, message);
    }

    public void critical(final String message) {
        log(Level.CRITICAL, message);
    }

    public void critical(final Throwable throwable) {
        log(Level.CRITICAL, throwable.toString(), throwable);
    }

    private void log(final Level level, final String message) {
        log(level, message, null);
    }

    private void log(final Level level, final String message, final Throwable throwable) {
        if(level.ordinal() > Logger.level.ordinal()) {
            try {
                ensureWriterConfigured();
                Logger.writer.println(new Date() + " [" + level.name() + "] " + name + " " + message);
                if (throwable != null) {
                    throwable.printStackTrace(Logger.writer);
                }
                Logger.writer.flush();
            } catch (Exception e) {
                // basically we're fucked; so ignore and soldier on...
            }
        }
    }

}
