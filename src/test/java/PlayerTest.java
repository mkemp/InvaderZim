import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    @Test
    public void enemy() {
        assertEquals(2, Player.ENEMY.getId());
        assertEquals("ENEMY", Player.ENEMY.getName());
        assertEquals("<Player 2: 'ENEMY'>", Player.ENEMY.toString());
    }

    @Test
    public void me() {
        assertEquals(1, Player.ME.getId());
        assertEquals("ME", Player.ME.getName());
        assertEquals("<Player 1: 'ME'>", Player.ME.toString());
    }

    @Test
    public void neutral() {
        assertEquals(0, Player.NEUTRAL.getId());
        assertEquals("NEUTRAL", Player.NEUTRAL.getName());
        assertEquals("<Player 0: 'NEUTRAL'>", Player.NEUTRAL.toString());
    }

    @Test
    public void everyone() {
        for (final Player player : Player.values()) {
            assertTrue(Player.EVERYONE.contains(player));
        }
    }

    @Test
    public void not_me() {
        for (final Player player : Player.values()) {
            if (Player.ME.equals(player)) {
                assertFalse(Player.NOT_ME.contains(player));
            } else {
                assertTrue(Player.NOT_ME.contains(player));
            }
        }
    }
}
