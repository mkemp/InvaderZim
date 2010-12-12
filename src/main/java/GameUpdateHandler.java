import java.util.regex.Pattern;

public interface GameUpdateHandler {
    
    static final Pattern SPACE_RE = Pattern.compile(" ");

    void turnDone();

    void turnStart(int turn);

    void update(String line);
}
