import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Player {

    NEUTRAL, ME, ENEMY;

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name();
    }

    public String toString() {
        return "<Player " + ordinal() + ": '" + name() + "'>";
    }

    public static final Set<Player> EVERYONE = Collections.unmodifiableSet(
        new HashSet<Player>(Arrays.asList(NEUTRAL, ME, ENEMY))
    );

    public static final Set<Player> NOT_ME = Collections.unmodifiableSet(
        new HashSet<Player>(Arrays.asList(NEUTRAL, ENEMY))
    );
    
    private static final Map<Integer,Player> MAP = new HashMap<Integer,Player>();
    static {
        for (final Player player : Player.values()) {
            MAP.put(player.ordinal(), player);
        }
    }

    public static Player get(final int id) {
        return MAP.get(id);
    }

}
